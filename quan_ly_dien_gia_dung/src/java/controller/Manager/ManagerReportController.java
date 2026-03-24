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
import java.util.List;
import model.User;

@WebServlet(name = "ManagerReportController", urlPatterns = {"/manager-report"})
public class ManagerReportController extends HttpServlet {

    private final ManagerReportDAO reportDAO = new ManagerReportDAO();

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

        int periodDays = (int) (toLocalDate.toEpochDay() - fromLocalDate.toEpochDay()) + 1;
        LocalDate prevToLocalDate = fromLocalDate.minusDays(1);
        LocalDate prevFromLocalDate = prevToLocalDate.minusDays(periodDays - 1L);
        Date prevFromDate = Date.valueOf(prevFromLocalDate);
        Date prevToDate = Date.valueOf(prevToLocalDate);

        ManagerReportDAO.SummaryMetrics summary = reportDAO.getSummaryMetrics(fromDate, toDate);
        ManagerReportDAO.SummaryMetrics previousSummary = reportDAO.getSummaryMetrics(prevFromDate, prevToDate);
        ManagerReportDAO.InventoryInsight inventoryInsight = reportDAO.getInventoryInsight(fromDate, toDate, summary.getTotalExportValue());
        List<ManagerReportDAO.TopVariantFlow> topVariantFlows = reportDAO.getTopVariantFlows(fromDate, toDate, 10);

        request.setAttribute("summary", summary);
        request.setAttribute("previousSummary", previousSummary);
        request.setAttribute("inventoryInsight", inventoryInsight);
        request.setAttribute("monthlyFlows", reportDAO.getMonthlyFlow(fromDate, toDate));
        request.setAttribute("topVariantFlows", topVariantFlows);

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

}