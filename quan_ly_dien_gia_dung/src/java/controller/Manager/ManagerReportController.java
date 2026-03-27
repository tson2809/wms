package controller.Manager;

import dal.ManagerReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import model.User;

@WebServlet(name = "ManagerReportController", urlPatterns = {"/manager-report", "/manager-report-variant-detail"})
public class ManagerReportController extends HttpServlet {

    private final ManagerReportDAO reportDAO = new ManagerReportDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roleName = loggedUser.getRole() != null && loggedUser.getRole().getRoleName() != null
                ? loggedUser.getRole().getRoleName().toLowerCase()
                : "";
        if (!"manager".equals(roleName)) {
            response.sendRedirect(request.getContextPath() + "/login?denied=true");
            return;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("/manager-report-variant-detail".equals(path)) {
            handleVariantDetail(request, response);
            return;
        }

        LocalDate[] range = resolveDateRange(request.getParameter("range"), request.getParameter("fromDate"), request.getParameter("toDate"));
        LocalDate fromLocalDate = range[0];
        LocalDate toLocalDate = range[1];

        if (fromLocalDate.isAfter(toLocalDate)) {
            LocalDate temp = fromLocalDate;
            fromLocalDate = toLocalDate;
            toLocalDate = temp;
        }

        Date fromDate = Date.valueOf(fromLocalDate);
        Date toDate = Date.valueOf(toLocalDate);

        String topMode = normalizeTopMode(request.getParameter("topMode"));
        int topPage = parsePositiveInt(request.getParameter("topPage"), 1);
        int topNumberPerPage = parseNumberPerPage(request.getParameter("topNumberPerPage"), 10);

        int periodDays = (int) (toLocalDate.toEpochDay() - fromLocalDate.toEpochDay()) + 1;
        LocalDate prevToLocalDate = fromLocalDate.minusDays(1);
        LocalDate prevFromLocalDate = prevToLocalDate.minusDays(periodDays - 1L);
        Date prevFromDate = Date.valueOf(prevFromLocalDate);
        Date prevToDate = Date.valueOf(prevToLocalDate);

        String invValueGranularity = resolveInventoryValueGranularity(
                request.getParameter("invValueGranularity"),
                periodDays
        );
        String invValueGranularityLabel = toGranularityLabel(invValueGranularity);

        ManagerReportDAO.SummaryMetrics summary = reportDAO.getSummaryMetrics(fromDate, toDate);
        ManagerReportDAO.SummaryMetrics previousSummary = reportDAO.getSummaryMetrics(prevFromDate, prevToDate);
        ManagerReportDAO.InventoryInsight inventoryInsight = reportDAO.getInventoryInsight(fromDate, toDate, summary.getTotalExportValue());
        // Load full rows for selected period, then paginate on server side.
        List<ManagerReportDAO.TopVariantFlow> topImportVariantsAll = reportDAO.getTopImportVariants(fromDate, toDate, Integer.MAX_VALUE);
        List<ManagerReportDAO.TopVariantFlow> topExportVariantsAll = reportDAO.getTopExportVariants(fromDate, toDate, Integer.MAX_VALUE);
        List<ManagerReportDAO.TopVariantStockRiskFlow> topStockRiskVariantsAll = reportDAO.getTopStockRiskVariants(toDate, Integer.MAX_VALUE);

        List<?> selectedRows;
        if ("export".equals(topMode)) {
            selectedRows = topExportVariantsAll;
        } else if ("risk".equals(topMode)) {
            selectedRows = topStockRiskVariantsAll;
        } else {
            selectedRows = topImportVariantsAll;
        }

        int topTotalRows = selectedRows.size();
        int topListOfPage = Math.max(1, (int) Math.ceil((double) topTotalRows / topNumberPerPage));
        if (topPage > topListOfPage) {
            topPage = topListOfPage;
        }
        List<?> topVariantRows = paginate(selectedRows, topPage, topNumberPerPage);

        List<ManagerReportDAO.InventoryValueCategoryRow> inventoryValueByCategory =
                reportDAO.getInventoryValueByCategory(toDate);
        List<ManagerReportDAO.InventoryValuePoint> inventoryValueTrend =
                reportDAO.getInventoryValueTrend(fromDate, toDate, invValueGranularity);

        if (inventoryValueByCategory == null) {
            inventoryValueByCategory = Collections.emptyList();
        }
        if (inventoryValueTrend == null) {
            inventoryValueTrend = Collections.emptyList();
        }

        BigDecimal inventoryValueTotal = BigDecimal.ZERO;
        for (ManagerReportDAO.InventoryValueCategoryRow row : inventoryValueByCategory) {
            if (row != null && row.getValue() != null) {
                inventoryValueTotal = inventoryValueTotal.add(row.getValue());
            }
        }

        request.setAttribute("summary", summary);
        request.setAttribute("previousSummary", previousSummary);
        request.setAttribute("inventoryInsight", inventoryInsight);
        request.setAttribute("monthlyFlows", reportDAO.getMonthlyFlow(fromDate, toDate));
        request.setAttribute("topVariantRows", topVariantRows);
        request.setAttribute("topMode", topMode);
        request.setAttribute("topPage", topPage);
        request.setAttribute("topListOfPage", topListOfPage);
        request.setAttribute("topNumberPerPage", topNumberPerPage);
        request.setAttribute("topTotalRows", topTotalRows);
        request.setAttribute("inventoryValueTotal", inventoryValueTotal);
        request.setAttribute("inventoryValueByCategory", inventoryValueByCategory);
        request.setAttribute("inventoryValueTrend", inventoryValueTrend);
        request.setAttribute("inventoryValueGranularity", invValueGranularity);
        request.setAttribute("inventoryValueGranularityLabel", invValueGranularityLabel);

        request.setAttribute("fromDate", fromLocalDate.toString());
        request.setAttribute("toDate", toLocalDate.toString());
        request.setAttribute("prevFromDate", prevFromLocalDate.toString());
        request.setAttribute("prevToDate", prevToLocalDate.toString());
        request.setAttribute("selectedRange", normalizeRange(request.getParameter("range")));

        request.setAttribute("deltaNetCashFlow", calculateDelta(summary.getNetCashFlow(), previousSummary.getNetCashFlow()));
        request.setAttribute("deltaImportValue", calculateDelta(summary.getTotalImportValue(), previousSummary.getTotalImportValue()));
        request.setAttribute("deltaExportValue", calculateDelta(summary.getTotalExportValue(), previousSummary.getTotalExportValue()));
        request.setAttribute("deltaSalesValue", calculateDelta(summary.getTotalSalesValue(), previousSummary.getTotalSalesValue()));

        request.getRequestDispatcher("/view/manager/manager-report.jsp").forward(request, response);
    }

    private void handleVariantDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sku = request.getParameter("sku");
        if (sku == null || sku.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/manager-report");
            return;
        }
        sku = sku.trim();
        String detailMode = normalizeDetailMode(request.getParameter("mode"));

        LocalDate[] range = resolveDateRange("custom", request.getParameter("fromDate"), request.getParameter("toDate"));
        LocalDate fromLocalDate = range[0];
        LocalDate toLocalDate = range[1];
        if (fromLocalDate.isAfter(toLocalDate)) {
            LocalDate temp = fromLocalDate;
            fromLocalDate = toLocalDate;
            toLocalDate = temp;
        }

        int page = parsePositiveInt(request.getParameter("page"), 1);
        int numberPerPage = parseNumberPerPage(request.getParameter("numberPerPage"), 10);

        Date fromDate = Date.valueOf(fromLocalDate);
        Date toDate = Date.valueOf(toLocalDate);

        int totalRows = reportDAO.countVariantOrderFlows(sku, fromDate, toDate, detailMode);
        int listOfPage = Math.max(1, (int) Math.ceil((double) totalRows / numberPerPage));
        if (page > listOfPage) {
            page = listOfPage;
        }

        int offset = (page - 1) * numberPerPage;
        List<ManagerReportDAO.VariantOrderFlow> rows;
        if (totalRows == 0) {
            rows = Collections.emptyList();
        } else {
            rows = reportDAO.getVariantOrderFlows(sku, fromDate, toDate, detailMode, offset, numberPerPage);
        }

        request.setAttribute("sku", sku);
        request.setAttribute("productName", reportDAO.getProductNameBySku(sku));
        request.setAttribute("rows", rows);
        request.setAttribute("fromDate", fromLocalDate.toString());
        request.setAttribute("toDate", toLocalDate.toString());
        request.setAttribute("detailMode", detailMode);
        request.setAttribute("page", page);
        request.setAttribute("listOfPage", listOfPage);
        request.setAttribute("numberPerPage", numberPerPage);
        request.setAttribute("totalRows", totalRows);

        request.getRequestDispatcher("/view/manager/manager-report-variant-detail.jsp").forward(request, response);
    }

    private LocalDate[] resolveDateRange(String rangeParam, String fromParam, String toParam) {
        LocalDate today = LocalDate.now();
        String range = normalizeRange(rangeParam);

        if ("today".equals(range)) {
            return new LocalDate[]{today, today};
        }
        if ("this_week".equals(range)) {
            LocalDate from = today.minusDays(today.getDayOfWeek().getValue() - 1L);
            return new LocalDate[]{from, today};
        }
        if ("this_month".equals(range)) {
            LocalDate from = today.withDayOfMonth(1);
            return new LocalDate[]{from, today};
        }
        if ("this_quarter".equals(range)) {
            int startMonth = ((today.getMonthValue() - 1) / 3) * 3 + 1;
            LocalDate from = LocalDate.of(today.getYear(), startMonth, 1);
            return new LocalDate[]{from, today};
        }
        if ("year".equals(range)) {
            LocalDate from = today.minusYears(1).plusDays(1);
            return new LocalDate[]{from, today};
        }
        if ("ytd".equals(range)) {
            LocalDate from = today.minusYears(1).plusDays(1);
            return new LocalDate[]{from, today};
        }

        LocalDate toDate = today;
        LocalDate fromDate = today.minusDays(29);
        try {
            if (fromParam != null && !fromParam.trim().isEmpty()) {
                fromDate = LocalDate.parse(fromParam.trim());
            }
            if (toParam != null && !toParam.trim().isEmpty()) {
                toDate = LocalDate.parse(toParam.trim());
            }
        } catch (Exception ignored) {
            fromDate = today.minusDays(29);
            toDate = today;
        }
        return new LocalDate[]{fromDate, toDate};
    }

    private String normalizeRange(String rangeParam) {
        if (rangeParam == null || rangeParam.trim().isEmpty()) {
            return "custom";
        }
        String range = rangeParam.trim().toLowerCase();
        switch (range) {
            case "today":
            case "this_week":
            case "this_month":
            case "this_quarter":
            case "year":
            case "ytd":
            case "custom":
                return range;
            default:
                return "custom";
        }
    }

    private String resolveInventoryValueGranularity(String granularityParam, int periodDays) {
        if (granularityParam == null || granularityParam.trim().isEmpty()) {
            return "month";
        }
        String g = granularityParam.trim().toLowerCase();
        switch (g) {
            case "day":
            case "week":
            case "month":
                // Guardrail: day granularity can become heavy on long ranges.
                if ("day".equals(g) && periodDays > 90) {
                    return "week";
                }
                return g;
            default:
                return "month";
        }
    }

    private String toGranularityLabel(String granularity) {
        if ("day".equals(granularity)) {
            return "Ngày";
        }
        if ("week".equals(granularity)) {
            return "Tuần";
        }
        return "Tháng";
    }

    private BigDecimal calculateDelta(BigDecimal current, BigDecimal previous) {
        if (current == null) {
            current = BigDecimal.ZERO;
        }
        if (previous == null) {
            previous = BigDecimal.ZERO;
        }
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
        }
        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous.abs(), 2, java.math.RoundingMode.HALF_UP);
    }

    private String normalizeTopMode(String topModeParam) {
        if (topModeParam == null || topModeParam.trim().isEmpty()) {
            return "import";
        }
        String mode = topModeParam.trim().toLowerCase();
        if ("export".equals(mode) || "risk".equals(mode)) {
            return mode;
        }
        return "import";
    }

    private String normalizeDetailMode(String modeParam) {
        if (modeParam == null || modeParam.trim().isEmpty()) {
            return "all";
        }
        String mode = modeParam.trim().toLowerCase();
        if ("port".equals(mode)) {
            return "import";
        }
        switch (mode) {
            case "import":
            case "export":
            case "risk":
            case "all":
                return mode;
            default:
                return "all";
        }
    }

    private int parsePositiveInt(String raw, int fallback) {
        try {
            int value = Integer.parseInt(raw);
            return value > 0 ? value : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private int parseNumberPerPage(String raw, int fallback) {
        int value = parsePositiveInt(raw, fallback);
        return (value == 5 || value == 10 || value == 20) ? value : fallback;
    }

    private List<?> paginate(List<?> rows, int page, int numberPerPage) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        int fromIndex = (page - 1) * numberPerPage;
        if (fromIndex >= rows.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + numberPerPage, rows.size());
        return rows.subList(fromIndex, toIndex);
    }

}