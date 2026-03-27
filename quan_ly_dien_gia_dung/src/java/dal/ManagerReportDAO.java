package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.RoundingMode;

public class ManagerReportDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ManagerReportDAO.class.getName());

    public static class SummaryMetrics {
        private BigDecimal totalImportValue = BigDecimal.ZERO;
        private BigDecimal totalExportValue = BigDecimal.ZERO;
        private BigDecimal totalPurchaseValue = BigDecimal.ZERO;
        private BigDecimal totalSalesValue = BigDecimal.ZERO;
        private BigDecimal supplierRefundValue = BigDecimal.ZERO;
        private BigDecimal customerRefundValue = BigDecimal.ZERO;
        private BigDecimal cashIn = BigDecimal.ZERO;
        private BigDecimal cashOut = BigDecimal.ZERO;
        private BigDecimal netCashFlow = BigDecimal.ZERO;
        private long totalImportQuantity;
        private long totalExportQuantity;
        private int completedReceiptCount;
        private int completedIssueCount;
        private int completedPurchaseOrderCount;
        private int completedSaleOrderCount;
        private int transactionCount;
        private BigDecimal estimatedReceivable = BigDecimal.ZERO;
        private BigDecimal estimatedPayable = BigDecimal.ZERO;

        public BigDecimal getTotalImportValue() {
            return totalImportValue;
        }

        public void setTotalImportValue(BigDecimal totalImportValue) {
            this.totalImportValue = totalImportValue;
        }

        public BigDecimal getTotalExportValue() {
            return totalExportValue;
        }

        public void setTotalExportValue(BigDecimal totalExportValue) {
            this.totalExportValue = totalExportValue;
        }

        public BigDecimal getTotalPurchaseValue() {
            return totalPurchaseValue;
        }

        public void setTotalPurchaseValue(BigDecimal totalPurchaseValue) {
            this.totalPurchaseValue = totalPurchaseValue;
        }

        public BigDecimal getTotalSalesValue() {
            return totalSalesValue;
        }

        public void setTotalSalesValue(BigDecimal totalSalesValue) {
            this.totalSalesValue = totalSalesValue;
        }

        public BigDecimal getSupplierRefundValue() {
            return supplierRefundValue;
        }

        public void setSupplierRefundValue(BigDecimal supplierRefundValue) {
            this.supplierRefundValue = supplierRefundValue;
        }

        public BigDecimal getCustomerRefundValue() {
            return customerRefundValue;
        }

        public void setCustomerRefundValue(BigDecimal customerRefundValue) {
            this.customerRefundValue = customerRefundValue;
        }

        public BigDecimal getCashIn() {
            return cashIn;
        }

        public void setCashIn(BigDecimal cashIn) {
            this.cashIn = cashIn;
        }

        public BigDecimal getCashOut() {
            return cashOut;
        }

        public void setCashOut(BigDecimal cashOut) {
            this.cashOut = cashOut;
        }

        public BigDecimal getNetCashFlow() {
            return netCashFlow;
        }

        public void setNetCashFlow(BigDecimal netCashFlow) {
            this.netCashFlow = netCashFlow;
        }

        public long getTotalImportQuantity() {
            return totalImportQuantity;
        }

        public void setTotalImportQuantity(long totalImportQuantity) {
            this.totalImportQuantity = totalImportQuantity;
        }

        public long getTotalExportQuantity() {
            return totalExportQuantity;
        }

        public void setTotalExportQuantity(long totalExportQuantity) {
            this.totalExportQuantity = totalExportQuantity;
        }

        public int getCompletedReceiptCount() {
            return completedReceiptCount;
        }

        public void setCompletedReceiptCount(int completedReceiptCount) {
            this.completedReceiptCount = completedReceiptCount;
        }

        public int getCompletedIssueCount() {
            return completedIssueCount;
        }

        public void setCompletedIssueCount(int completedIssueCount) {
            this.completedIssueCount = completedIssueCount;
        }

        public int getCompletedPurchaseOrderCount() {
            return completedPurchaseOrderCount;
        }

        public void setCompletedPurchaseOrderCount(int completedPurchaseOrderCount) {
            this.completedPurchaseOrderCount = completedPurchaseOrderCount;
        }

        public int getCompletedSaleOrderCount() {
            return completedSaleOrderCount;
        }

        public void setCompletedSaleOrderCount(int completedSaleOrderCount) {
            this.completedSaleOrderCount = completedSaleOrderCount;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(int transactionCount) {
            this.transactionCount = transactionCount;
        }

        public BigDecimal getEstimatedReceivable() {
            return estimatedReceivable;
        }

        public void setEstimatedReceivable(BigDecimal estimatedReceivable) {
            this.estimatedReceivable = estimatedReceivable;
        }

        public BigDecimal getEstimatedPayable() {
            return estimatedPayable;
        }

        public void setEstimatedPayable(BigDecimal estimatedPayable) {
            this.estimatedPayable = estimatedPayable;
        }
    }

    public static class InventoryInsight {
        private long openingQuantity;
        private long closingQuantity;
        private BigDecimal openingValue = BigDecimal.ZERO;
        private BigDecimal closingValue = BigDecimal.ZERO;
        private BigDecimal averageInventoryValue = BigDecimal.ZERO;
        private BigDecimal stockTurnover = BigDecimal.ZERO;
        private int deadStockCount;
        private int stockoutCount;

        public long getOpeningQuantity() {
            return openingQuantity;
        }

        public void setOpeningQuantity(long openingQuantity) {
            this.openingQuantity = openingQuantity;
        }

        public long getClosingQuantity() {
            return closingQuantity;
        }

        public void setClosingQuantity(long closingQuantity) {
            this.closingQuantity = closingQuantity;
        }

        public BigDecimal getOpeningValue() {
            return openingValue;
        }

        public void setOpeningValue(BigDecimal openingValue) {
            this.openingValue = openingValue;
        }

        public BigDecimal getClosingValue() {
            return closingValue;
        }

        public void setClosingValue(BigDecimal closingValue) {
            this.closingValue = closingValue;
        }

        public BigDecimal getAverageInventoryValue() {
            return averageInventoryValue;
        }

        public void setAverageInventoryValue(BigDecimal averageInventoryValue) {
            this.averageInventoryValue = averageInventoryValue;
        }

        public BigDecimal getStockTurnover() {
            return stockTurnover;
        }

        public void setStockTurnover(BigDecimal stockTurnover) {
            this.stockTurnover = stockTurnover;
        }

        public int getDeadStockCount() {
            return deadStockCount;
        }

        public void setDeadStockCount(int deadStockCount) {
            this.deadStockCount = deadStockCount;
        }

        public int getStockoutCount() {
            return stockoutCount;
        }

        public void setStockoutCount(int stockoutCount) {
            this.stockoutCount = stockoutCount;
        }
    }

    public static class InventoryValueCategoryRow {
        private String categoryName;
        private BigDecimal value = BigDecimal.ZERO;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }

    public static class InventoryValuePoint {
        private String label;
        private BigDecimal value = BigDecimal.ZERO;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }

    public static class MonthlyFlow {
        private String monthLabel;
        private BigDecimal importValue = BigDecimal.ZERO;
        private BigDecimal exportValue = BigDecimal.ZERO;
        private BigDecimal salesValue = BigDecimal.ZERO;

        public String getMonthLabel() {
            return monthLabel;
        }

        public void setMonthLabel(String monthLabel) {
            this.monthLabel = monthLabel;
        }

        public BigDecimal getImportValue() {
            return importValue;
        }

        public void setImportValue(BigDecimal importValue) {
            this.importValue = importValue;
        }

        public BigDecimal getExportValue() {
            return exportValue;
        }

        public void setExportValue(BigDecimal exportValue) {
            this.exportValue = exportValue;
        }

        public BigDecimal getSalesValue() {
            return salesValue;
        }

        public void setSalesValue(BigDecimal salesValue) {
            this.salesValue = salesValue;
        }
    }

    public static class IssueTypeStat {
        private String issueType;
        private int issueCount;

        public String getIssueType() {
            return issueType;
        }

        public void setIssueType(String issueType) {
            this.issueType = issueType;
        }

        public int getIssueCount() {
            return issueCount;
        }

        public void setIssueCount(int issueCount) {
            this.issueCount = issueCount;
        }
    }

    public static class TopVariantFlow {
        private String sku;
        private String productName;
        private long importQuantity;
        private long exportQuantity;
        private long netQuantity;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public long getImportQuantity() {
            return importQuantity;
        }

        public void setImportQuantity(long importQuantity) {
            this.importQuantity = importQuantity;
        }

        public long getExportQuantity() {
            return exportQuantity;
        }

        public void setExportQuantity(long exportQuantity) {
            this.exportQuantity = exportQuantity;
        }

        public long getNetQuantity() {
            return netQuantity;
        }

        public void setNetQuantity(long netQuantity) {
            this.netQuantity = netQuantity;
        }
    }

    public static class TopVariantStockRiskFlow {
        private String sku;
        private String productName;
        private long stockQuantity;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public long getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(long stockQuantity) {
            this.stockQuantity = stockQuantity;
        }
    }

    public static class VariantOrderFlow {
        private String flowType;
        private Integer documentId;
        private String documentCode;
        private String orderCode;
        private Timestamp flowDate;
        private long quantity;
        private String status;

        public String getFlowType() {
            return flowType;
        }

        public void setFlowType(String flowType) {
            this.flowType = flowType;
        }

        public Integer getDocumentId() {
            return documentId;
        }

        public void setDocumentId(Integer documentId) {
            this.documentId = documentId;
        }

        public String getDocumentCode() {
            return documentCode;
        }

        public void setDocumentCode(String documentCode) {
            this.documentCode = documentCode;
        }

        public String getOrderCode() {
            return orderCode;
        }

        public void setOrderCode(String orderCode) {
            this.orderCode = orderCode;
        }

        public Timestamp getFlowDate() {
            return flowDate;
        }

        public void setFlowDate(Timestamp flowDate) {
            this.flowDate = flowDate;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public SummaryMetrics getSummaryMetrics(Date fromDate, Date toDate) {
        SummaryMetrics summary = new SummaryMetrics();

        summary.setTotalImportValue(queryDecimal(
                "SELECT COALESCE(SUM(total_amount),0) FROM goods_receipts WHERE status = 'completed' AND receipt_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setTotalExportValue(queryDecimal(
                "SELECT COALESCE(SUM(gid.quantity * pv.cost_price),0) "
                + "FROM goods_issues gi "
                + "JOIN goods_issue_details gid ON gi.issue_id = gid.issue_id "
                + "JOIN product_variants pv ON gid.variant_id = pv.variant_id "
                + "WHERE gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setTotalPurchaseValue(queryDecimal(
                "SELECT COALESCE(SUM(total_amount),0) FROM purchase_orders "
                + "WHERE supplier_id IS NOT NULL AND status IN ('approved','received') AND order_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setTotalSalesValue(queryDecimal(
                "SELECT COALESCE(SUM(total_amount),0) FROM purchase_orders "
                + "WHERE supplier_id IS NULL AND status IN ('approved','received') AND order_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setSupplierRefundValue(queryDecimal(
                "SELECT COALESCE(SUM(total_refund_amount),0) FROM return_orders "
                + "WHERE refund_status = 'refunded' AND status IN ('processing','completed') AND return_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setCustomerRefundValue(queryDecimal(
                "SELECT COALESCE(SUM(srd.total_refund),0) "
                + "FROM sales_returns sr "
                + "JOIN sales_return_details srd ON sr.sales_return_id = srd.sales_return_id "
                + "WHERE sr.refund_status = 'refunded' AND sr.status IN ('processing','completed') "
                + "AND DATE(sr.return_date) BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setTotalImportQuantity(queryLong(
                "SELECT COALESCE(SUM(grd.quantity),0) "
                + "FROM goods_receipts gr "
                + "JOIN goods_receipt_details grd ON gr.receipt_id = grd.receipt_id "
                + "WHERE gr.status = 'completed' AND gr.receipt_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setTotalExportQuantity(queryLong(
                "SELECT COALESCE(SUM(gid.quantity),0) "
                + "FROM goods_issues gi "
                + "JOIN goods_issue_details gid ON gi.issue_id = gid.issue_id "
                + "WHERE gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setCompletedReceiptCount(queryInt(
                "SELECT COUNT(*) FROM goods_receipts WHERE status = 'completed' AND receipt_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setCompletedIssueCount(queryInt(
            "SELECT COUNT(*) FROM goods_issues WHERE status = 'completed' AND DATE(issue_date) BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setCompletedPurchaseOrderCount(queryInt(
                "SELECT COUNT(*) FROM purchase_orders "
                + "WHERE supplier_id IS NOT NULL AND status IN ('approved','received') AND order_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setCompletedSaleOrderCount(queryInt(
                "SELECT COUNT(*) FROM purchase_orders "
                + "WHERE supplier_id IS NULL AND status IN ('approved','received') AND order_date BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setTransactionCount(queryInt(
                "SELECT COUNT(*) FROM inventory_transactions WHERE DATE(transaction_date) BETWEEN ? AND ?",
                fromDate,
                toDate));

        summary.setCashIn(summary.getTotalSalesValue().add(summary.getSupplierRefundValue()));
        summary.setCashOut(summary.getTotalPurchaseValue().add(summary.getCustomerRefundValue()));
        summary.setNetCashFlow(summary.getCashIn().subtract(summary.getCashOut()));

        // Estimated AR/AP because the current schema has no dedicated payment ledger.
        summary.setEstimatedReceivable(summary.getTotalSalesValue().subtract(summary.getCustomerRefundValue()));
        summary.setEstimatedPayable(summary.getTotalPurchaseValue().subtract(summary.getSupplierRefundValue()));

        return summary;
    }

        public InventoryInsight getInventoryInsight(Date fromDate, Date toDate, BigDecimal periodExportValue) {
        InventoryInsight insight = new InventoryInsight();

        insight.setClosingQuantity(queryLongNoRange(
            "SELECT COALESCE(SUM(quantity),0) FROM product_variants WHERE status = 'active'"));

        insight.setClosingValue(queryDecimalNoRange(
            "SELECT COALESCE(SUM(quantity * cost_price),0) FROM product_variants WHERE status = 'active'"));

        long netPeriodMovement = queryLong(
            "SELECT COALESCE(SUM(quantity_change),0) FROM inventory_transactions WHERE DATE(transaction_date) BETWEEN ? AND ?",
            fromDate,
            toDate);

        BigDecimal netPeriodValue = queryDecimal(
            "SELECT COALESCE(SUM(quantity_change * pv.cost_price),0) "
            + "FROM inventory_transactions it "
            + "JOIN product_variants pv ON it.variant_id = pv.variant_id "
            + "WHERE DATE(it.transaction_date) BETWEEN ? AND ?",
            fromDate,
            toDate);

        insight.setOpeningQuantity(insight.getClosingQuantity() - netPeriodMovement);
        insight.setOpeningValue(insight.getClosingValue().subtract(netPeriodValue));

        BigDecimal avgValue = insight.getOpeningValue().add(insight.getClosingValue())
            .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        insight.setAverageInventoryValue(avgValue);

        if (avgValue.compareTo(BigDecimal.ZERO) > 0) {
            insight.setStockTurnover(periodExportValue.divide(avgValue, 2, RoundingMode.HALF_UP));
        } else {
            insight.setStockTurnover(BigDecimal.ZERO);
        }

        insight.setDeadStockCount(queryIntNoRange(
            "SELECT COUNT(*) FROM product_variants pv "
            + "WHERE pv.quantity > 0 AND pv.status = 'active' "
            + "AND NOT EXISTS ("
            + "    SELECT 1 FROM inventory_transactions it "
            + "    WHERE it.variant_id = pv.variant_id AND DATE(it.transaction_date) BETWEEN ? AND ?"
            + ")",
            fromDate,
            toDate));

        insight.setStockoutCount(queryIntNoRange(
            "SELECT COUNT(*) FROM product_variants WHERE status = 'active' AND quantity <= 0"));

        return insight;
        }

    /**
     * Inventory Value theo category (snapshot as-of @asOfDate).
     * Định giá theo cost_price hiện tại (product_variants.cost_price).
     */
    public List<InventoryValueCategoryRow> getInventoryValueByCategory(Date asOfDate) {
        Map<String, BigDecimal> baseByCategory = new LinkedHashMap<>();
        Map<String, BigDecimal> movementAfterByCategory = new LinkedHashMap<>();

        String baseSql = "SELECT c.category_name AS category_name, "
                + "COALESCE(SUM(pv.quantity * pv.cost_price),0) AS base_value "
                + "FROM categories c "
                + "JOIN products p ON p.category_id = c.category_id "
                + "JOIN product_variants pv ON pv.product_id = p.product_id "
                + "WHERE pv.status = 'active' "
                + "GROUP BY c.category_id, c.category_name";

        String movementSql = "SELECT c.category_name AS category_name, "
                + "COALESCE(SUM(it.quantity_change * pv.cost_price),0) AS movement_after_value "
                + "FROM inventory_transactions it "
                + "JOIN product_variants pv ON it.variant_id = pv.variant_id "
                + "JOIN products p ON pv.product_id = p.product_id "
                + "JOIN categories c ON p.category_id = c.category_id "
                + "WHERE pv.status = 'active' AND DATE(it.transaction_date) > ? "
                + "GROUP BY c.category_id, c.category_name";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(baseSql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                BigDecimal baseValue = rs.getBigDecimal("base_value");
                baseByCategory.put(categoryName, baseValue == null ? BigDecimal.ZERO : baseValue);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(movementSql)) {
            ps.setDate(1, asOfDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                BigDecimal movementAfterValue = rs.getBigDecimal("movement_after_value");
                movementAfterByCategory.put(categoryName, movementAfterValue == null ? BigDecimal.ZERO : movementAfterValue);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        List<InventoryValueCategoryRow> rows = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : baseByCategory.entrySet()) {
            String categoryName = e.getKey();
            BigDecimal baseValue = e.getValue() == null ? BigDecimal.ZERO : e.getValue();
            BigDecimal movementAfterValue = movementAfterByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            BigDecimal valueAtAsOf = baseValue.subtract(movementAfterValue);

            InventoryValueCategoryRow row = new InventoryValueCategoryRow();
            row.setCategoryName(categoryName);
            row.setValue(valueAtAsOf);
            rows.add(row);
        }

        rows.sort((a, b) -> {
            if (a == null || a.getValue() == null) {
                return 1;
            }
            if (b == null || b.getValue() == null) {
                return -1;
            }
            return b.getValue().compareTo(a.getValue());
        });
        return rows;
    }

    /**
     * Chuỗi snapshot Inventory Value theo thời gian (end of day/week/month),
     * định giá theo cost_price hiện tại.
     */
    public List<InventoryValuePoint> getInventoryValueTrend(Date fromDate, Date toDate, String granularity) {
        BigDecimal baseTotalValue = queryDecimalNoRange(
                "SELECT COALESCE(SUM(quantity * cost_price),0) FROM product_variants WHERE status = 'active'"
        );

        List<LocalDate> moveDates = new ArrayList<>();
        List<BigDecimal> moveValues = new ArrayList<>();

        String movementByDateSql = "SELECT DATE(it.transaction_date) AS trx_day, "
                + "COALESCE(SUM(it.quantity_change * pv.cost_price),0) AS movement_value "
                + "FROM inventory_transactions it "
                + "JOIN product_variants pv ON it.variant_id = pv.variant_id "
                + "WHERE pv.status = 'active' AND DATE(it.transaction_date) > ? "
                + "GROUP BY DATE(it.transaction_date) "
                + "ORDER BY trx_day ASC";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(movementByDateSql)) {
            ps.setDate(1, fromDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate day = rs.getDate("trx_day").toLocalDate();
                BigDecimal movementValue = rs.getBigDecimal("movement_value");
                moveDates.add(day);
                moveValues.add(movementValue == null ? BigDecimal.ZERO : movementValue);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        int n = moveDates.size();
        BigDecimal[] suffix = new BigDecimal[n + 1];
        suffix[n] = BigDecimal.ZERO;
        for (int i = n - 1; i >= 0; i--) {
            suffix[i] = suffix[i + 1].add(moveValues.get(i));
        }

        LocalDate fromLocalDate = fromDate.toLocalDate();
        LocalDate toLocalDate = toDate.toLocalDate();

        List<InventoryValuePoint> points = new ArrayList<>();

        if ("day".equalsIgnoreCase(granularity)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (LocalDate d = fromLocalDate; !d.isAfter(toLocalDate); d = d.plusDays(1)) {
                BigDecimal valueAt = calculateValueAt(d, baseTotalValue, moveDates, suffix);
                InventoryValuePoint p = new InventoryValuePoint();
                p.setLabel(d.format(fmt));
                p.setValue(valueAt);
                points.add(p);
            }
            return points;
        }

        if ("week".equalsIgnoreCase(granularity)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
            LocalDate cursor = fromLocalDate;
            while (!cursor.isAfter(toLocalDate)) {
                LocalDate weekEnd = cursor.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                if (weekEnd.isAfter(toLocalDate)) {
                    weekEnd = toLocalDate;
                }
                LocalDate weekStart = weekEnd.minusDays(6);

                BigDecimal valueAt = calculateValueAt(weekEnd, baseTotalValue, moveDates, suffix);
                InventoryValuePoint p = new InventoryValuePoint();
                p.setLabel(weekStart.format(fmt) + "-" + weekEnd.format(fmt));
                p.setValue(valueAt);
                points.add(p);

                cursor = weekEnd.plusDays(1);
            }
            return points;
        }

        // Default: month
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth ymFrom = YearMonth.from(fromLocalDate);
        YearMonth ymTo = YearMonth.from(toLocalDate);
        for (YearMonth ym = ymFrom; !ym.isAfter(ymTo); ym = ym.plusMonths(1)) {
            LocalDate monthEnd = ym.atEndOfMonth();
            if (monthEnd.isAfter(toLocalDate)) {
                monthEnd = toLocalDate;
            }

            BigDecimal valueAt = calculateValueAt(monthEnd, baseTotalValue, moveDates, suffix);
            InventoryValuePoint p = new InventoryValuePoint();
            p.setLabel(monthEnd.format(monthFmt));
            p.setValue(valueAt);
            points.add(p);
        }
        return points;
    }

    private BigDecimal calculateValueAt(
            LocalDate endDate,
            BigDecimal baseTotalValue,
            List<LocalDate> moveDates,
            BigDecimal[] suffix
    ) {
        int idx = upperBound(moveDates, endDate);
        BigDecimal sumAfter = suffix[idx];
        return baseTotalValue.subtract(sumAfter);
    }

    // Return index of the first element > target
    private int upperBound(List<LocalDate> sortedDates, LocalDate target) {
        int low = 0;
        int high = sortedDates.size();
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (sortedDates.get(mid).isAfter(target)) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    public List<MonthlyFlow> getMonthlyFlow(Date fromDate, Date toDate) {
        LocalDate start = fromDate.toLocalDate().withDayOfMonth(1);
        LocalDate end = toDate.toLocalDate().withDayOfMonth(1);

        Map<String, MonthlyFlow> monthMap = new LinkedHashMap<>();
        DateTimeFormatter keyFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MM/yyyy");

        for (LocalDate cursor = start; !cursor.isAfter(end); cursor = cursor.plusMonths(1)) {
            MonthlyFlow flow = new MonthlyFlow();
            flow.setMonthLabel(cursor.format(labelFormatter));
            monthMap.put(cursor.format(keyFormatter), flow);
        }

        fillMonthlyData(monthMap,
                "SELECT DATE_FORMAT(receipt_date, '%Y-%m') AS month_key, COALESCE(SUM(total_amount),0) AS value "
                + "FROM goods_receipts WHERE status = 'completed' AND receipt_date BETWEEN ? AND ? "
                + "GROUP BY DATE_FORMAT(receipt_date, '%Y-%m')",
                fromDate,
                toDate,
                "import");

        fillMonthlyData(monthMap,
                "SELECT DATE_FORMAT(issue_date, '%Y-%m') AS month_key, COALESCE(SUM(gid.quantity * pv.cost_price),0) AS value "
                + "FROM goods_issues gi "
                + "JOIN goods_issue_details gid ON gi.issue_id = gid.issue_id "
                + "JOIN product_variants pv ON gid.variant_id = pv.variant_id "
                + "WHERE gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ? "
                + "GROUP BY DATE_FORMAT(issue_date, '%Y-%m')",
                fromDate,
                toDate,
                "export");

        fillMonthlyData(monthMap,
                "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month_key, COALESCE(SUM(total_amount),0) AS value "
                + "FROM purchase_orders "
                + "WHERE supplier_id IS NULL AND status IN ('approved','received') AND order_date BETWEEN ? AND ? "
                + "GROUP BY DATE_FORMAT(order_date, '%Y-%m')",
                fromDate,
                toDate,
                "sales");

        return new ArrayList<>(monthMap.values());
    }

    public List<IssueTypeStat> getIssueTypeStats(Date fromDate, Date toDate) {
        List<IssueTypeStat> stats = new ArrayList<>();
        String sql = "SELECT issue_type, COUNT(*) AS issue_count "
                + "FROM goods_issues "
            + "WHERE status = 'completed' AND DATE(issue_date) BETWEEN ? AND ? "
                + "GROUP BY issue_type ORDER BY issue_count DESC";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                IssueTypeStat stat = new IssueTypeStat();
                stat.setIssueType(rs.getString("issue_type"));
                stat.setIssueCount(rs.getInt("issue_count"));
                stats.add(stat);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return stats;
    }

    public List<TopVariantFlow> getTopVariantFlows(Date fromDate, Date toDate, int limit) {
        List<TopVariantFlow> rows = new ArrayList<>();
        String sql = "SELECT pv.sku, p.product_name, "
                + "COALESCE(imp.import_qty,0) AS import_qty, "
                + "COALESCE(exp.export_qty,0) AS export_qty, "
                + "(COALESCE(imp.import_qty,0) - COALESCE(exp.export_qty,0)) AS net_qty "
                + "FROM product_variants pv "
                + "JOIN products p ON pv.product_id = p.product_id "
                + "LEFT JOIN ( "
                + "    SELECT grd.variant_id, SUM(grd.quantity) AS import_qty "
                + "    FROM goods_receipt_details grd "
                + "    JOIN goods_receipts gr ON gr.receipt_id = grd.receipt_id "
                + "    WHERE gr.status = 'completed' AND gr.receipt_date BETWEEN ? AND ? "
                + "    GROUP BY grd.variant_id "
                + ") imp ON imp.variant_id = pv.variant_id "
                + "LEFT JOIN ( "
                + "    SELECT gid.variant_id, SUM(gid.quantity) AS export_qty "
                + "    FROM goods_issue_details gid "
                + "    JOIN goods_issues gi ON gi.issue_id = gid.issue_id "
                + "    WHERE gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ? "
                + "    GROUP BY gid.variant_id "
                + ") exp ON exp.variant_id = pv.variant_id "
                + "WHERE COALESCE(imp.import_qty,0) > 0 OR COALESCE(exp.export_qty,0) > 0 "
                + "ORDER BY (COALESCE(imp.import_qty,0) + COALESCE(exp.export_qty,0)) DESC "
                + "LIMIT ?";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            pre.setDate(3, fromDate);
            pre.setDate(4, toDate);
            pre.setInt(5, limit);

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                TopVariantFlow row = new TopVariantFlow();
                row.setSku(rs.getString("sku"));
                row.setProductName(rs.getString("product_name"));
                row.setImportQuantity(rs.getLong("import_qty"));
                row.setExportQuantity(rs.getLong("export_qty"));
                row.setNetQuantity(rs.getLong("net_qty"));
                rows.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return rows;
    }

    public List<TopVariantFlow> getTopImportVariants(Date fromDate, Date toDate, int limit) {
        List<TopVariantFlow> rows = new ArrayList<>();
        String sql = "SELECT pv.sku, p.product_name, "
                + "COALESCE(SUM(grd.quantity),0) AS import_qty "
                + "FROM product_variants pv "
                + "JOIN products p ON pv.product_id = p.product_id "
                + "JOIN goods_receipt_details grd ON grd.variant_id = pv.variant_id "
                + "JOIN goods_receipts gr ON gr.receipt_id = grd.receipt_id "
                + "WHERE gr.status = 'completed' AND gr.receipt_date BETWEEN ? AND ? "
                + "GROUP BY pv.variant_id, pv.sku, p.product_name "
                + "ORDER BY import_qty DESC "
                + "LIMIT ?";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            pre.setInt(3, limit);

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                TopVariantFlow row = new TopVariantFlow();
                row.setSku(rs.getString("sku"));
                row.setProductName(rs.getString("product_name"));
                long importQty = rs.getLong("import_qty");
                row.setImportQuantity(importQty);
                row.setExportQuantity(0L);
                row.setNetQuantity(importQty);
                rows.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return rows;
    }

    public List<TopVariantFlow> getTopExportVariants(Date fromDate, Date toDate, int limit) {
        List<TopVariantFlow> rows = new ArrayList<>();
        String sql = "SELECT pv.sku, p.product_name, "
                + "COALESCE(SUM(gid.quantity),0) AS export_qty "
                + "FROM product_variants pv "
                + "JOIN products p ON pv.product_id = p.product_id "
                + "JOIN goods_issue_details gid ON gid.variant_id = pv.variant_id "
                + "JOIN goods_issues gi ON gi.issue_id = gid.issue_id "
                + "WHERE gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ? "
                + "GROUP BY pv.variant_id, pv.sku, p.product_name "
                + "ORDER BY export_qty DESC "
                + "LIMIT ?";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            pre.setInt(3, limit);

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                TopVariantFlow row = new TopVariantFlow();
                row.setSku(rs.getString("sku"));
                row.setProductName(rs.getString("product_name"));
                long exportQty = rs.getLong("export_qty");
                row.setImportQuantity(0L);
                row.setExportQuantity(exportQty);
                row.setNetQuantity(-exportQty);
                rows.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return rows;
    }

    public List<TopVariantStockRiskFlow> getTopStockRiskVariants(Date toDate, int limit) {
        List<TopVariantStockRiskFlow> rows = new ArrayList<>();
        String sql = "SELECT pv.sku, p.product_name, "
                + "(pv.quantity - COALESCE(after_qty.after_qty,0)) AS stock_qty "
                + "FROM product_variants pv "
                + "JOIN products p ON pv.product_id = p.product_id "
                + "LEFT JOIN ( "
                + "    SELECT variant_id, SUM(quantity_change) AS after_qty "
                + "    FROM inventory_transactions "
                + "    WHERE DATE(transaction_date) > ? "
                + "    GROUP BY variant_id "
                + ") after_qty ON after_qty.variant_id = pv.variant_id "
                + "WHERE pv.status = 'active' "
                + "ORDER BY stock_qty DESC "
                + "LIMIT ?";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, toDate);
            pre.setInt(2, limit);

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                TopVariantStockRiskFlow row = new TopVariantStockRiskFlow();
                row.setSku(rs.getString("sku"));
                row.setProductName(rs.getString("product_name"));
                row.setStockQuantity(rs.getLong("stock_qty"));
                rows.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return rows;
    }

    public String getProductNameBySku(String sku) {
        String sql = "SELECT p.product_name "
                + "FROM product_variants pv "
                + "JOIN products p ON p.product_id = pv.product_id "
                + "WHERE pv.sku = ? LIMIT 1";
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, sku);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getString("product_name");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int countVariantOrderFlows(String sku, Date fromDate, Date toDate, String mode) {
        String sql = "SELECT COUNT(*) FROM ("
                + " SELECT gr.receipt_id AS flow_id "
                + " FROM goods_receipt_details grd "
                + " JOIN goods_receipts gr ON gr.receipt_id = grd.receipt_id "
                + " JOIN product_variants pv ON pv.variant_id = grd.variant_id "
                + " WHERE pv.sku = ? AND gr.status = 'completed' AND gr.receipt_date BETWEEN ? AND ? "
                + "   AND (? <> 'export') "
                + " UNION ALL "
                + " SELECT gi.issue_id AS flow_id "
                + " FROM goods_issue_details gid "
                + " JOIN goods_issues gi ON gi.issue_id = gid.issue_id "
                + " JOIN product_variants pv ON pv.variant_id = gid.variant_id "
                + " WHERE pv.sku = ? AND gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ? "
                + "   AND (? <> 'import') "
                + ") t";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, sku);
            pre.setDate(2, fromDate);
            pre.setDate(3, toDate);
            pre.setString(4, mode);
            pre.setString(5, sku);
            pre.setDate(6, fromDate);
            pre.setDate(7, toDate);
            pre.setString(8, mode);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<VariantOrderFlow> getVariantOrderFlows(String sku, Date fromDate, Date toDate, String mode, int offset, int limit) {
        List<VariantOrderFlow> rows = new ArrayList<>();
        String sql = "SELECT x.flow_type, x.document_id, x.document_code, x.order_code, x.flow_date, x.quantity, x.status "
                + "FROM ( "
                + "  SELECT 'Nhập' AS flow_type, gr.receipt_id AS document_id, gr.receipt_code AS document_code, "
                + "         COALESCE(po.po_code, sr.sr_code, '-') AS order_code, "
                + "         CAST(gr.receipt_date AS DATETIME) AS flow_date, "
                + "         grd.quantity AS quantity, gr.status AS status "
                + "  FROM goods_receipt_details grd "
                + "  JOIN goods_receipts gr ON gr.receipt_id = grd.receipt_id "
                + "  JOIN product_variants pv ON pv.variant_id = grd.variant_id "
                + "  LEFT JOIN purchase_orders po ON po.purchase_order_id = gr.purchase_order_id "
                + "  LEFT JOIN sales_returns sr ON sr.sales_return_id = gr.sales_return_id "
                + "  WHERE pv.sku = ? AND gr.status = 'completed' AND gr.receipt_date BETWEEN ? AND ? "
                + "    AND (? <> 'export') "
                + "  UNION ALL "
                + "  SELECT 'Xuất' AS flow_type, gi.issue_id AS document_id, gi.issue_code AS document_code, "
                + "         COALESCE(po_note.po_code, ro.return_code, '-') AS order_code, "
                + "         gi.issue_date AS flow_date, "
                + "         gid.quantity AS quantity, gi.status AS status "
                + "  FROM goods_issue_details gid "
                + "  JOIN goods_issues gi ON gi.issue_id = gid.issue_id "
                + "  JOIN product_variants pv ON pv.variant_id = gid.variant_id "
                + "  LEFT JOIN return_orders ro ON ro.return_order_id = gi.return_order_id "
                + "  LEFT JOIN purchase_orders po_note "
                + "    ON po_note.purchase_order_id = CASE "
                + "         WHEN gi.notes LIKE '%[PO_ID:%]%' "
                + "         THEN CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(gi.notes, '[PO_ID:', -1), ']', 1) AS UNSIGNED) "
                + "         ELSE NULL END "
                + "  WHERE pv.sku = ? AND gi.status = 'completed' AND DATE(gi.issue_date) BETWEEN ? AND ? "
                + "    AND (? <> 'import') "
                + ") x "
                + "ORDER BY x.flow_date DESC "
                + "LIMIT ? OFFSET ?";

        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, sku);
            pre.setDate(2, fromDate);
            pre.setDate(3, toDate);
            pre.setString(4, mode);
            pre.setString(5, sku);
            pre.setDate(6, fromDate);
            pre.setDate(7, toDate);
            pre.setString(8, mode);
            pre.setInt(9, limit);
            pre.setInt(10, offset);

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                VariantOrderFlow row = new VariantOrderFlow();
                row.setFlowType(rs.getString("flow_type"));
                Object documentIdObj = rs.getObject("document_id");
                if (documentIdObj instanceof Number) {
                    row.setDocumentId(((Number) documentIdObj).intValue());
                }
                row.setDocumentCode(rs.getString("document_code"));
                row.setOrderCode(rs.getString("order_code"));
                row.setFlowDate(rs.getTimestamp("flow_date"));
                row.setQuantity(rs.getLong("quantity"));
                row.setStatus(rs.getString("status"));
                rows.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return rows;
    }

    private void fillMonthlyData(Map<String, MonthlyFlow> monthMap, String sql, Date fromDate, Date toDate, String type) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            ResultSet rs = pre.executeQuery();

            while (rs.next()) {
                String key = rs.getString("month_key");
                MonthlyFlow flow = monthMap.get(key);
                if (flow == null) {
                    continue;
                }

                BigDecimal value = rs.getBigDecimal("value");
                if (value == null) {
                    value = BigDecimal.ZERO;
                }

                if ("import".equals(type)) {
                    flow.setImportValue(value);
                } else if ("export".equals(type)) {
                    flow.setExportValue(value);
                } else if ("sales".equals(type)) {
                    flow.setSalesValue(value);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private BigDecimal queryDecimal(String sql, Date fromDate, Date toDate) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal(1);
                return value == null ? BigDecimal.ZERO : value;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return BigDecimal.ZERO;
    }

    private long queryLong(String sql, Date fromDate, Date toDate) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0L;
    }

    private int queryInt(String sql, Date fromDate, Date toDate) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private BigDecimal queryDecimalNoRange(String sql) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal(1);
                return value == null ? BigDecimal.ZERO : value;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return BigDecimal.ZERO;
    }

    private long queryLongNoRange(String sql) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0L;
    }

    private int queryIntNoRange(String sql, Date fromDate, Date toDate) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setDate(1, fromDate);
            pre.setDate(2, toDate);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private int queryIntNoRange(String sql) {
        try (Connection con = getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}