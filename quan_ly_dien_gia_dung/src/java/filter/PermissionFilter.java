package filter;

import dal.RoleDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.User;

@WebFilter(filterName = "PermissionFilter", urlPatterns = {
    "/goods-receipt-add",
    "/goods-receipt-edit",
    "/goods-receipt-list",
    "/goods-issue-add",
    "/goods-issue-detail",
    "/goods-issue-list",
    "/inventory-list",
    "/inventory-detail",
    "/inventory-alert",
    "/inventory-transactions",
    "/transaction-detail",
    "/inventory-sheet-list",
    "/inventory-sheet-create",
    "/inventory-sheet-view",
    "/inventory-sheet-edit",
    "/inventory-sheet-approve",
    "/export-inventory",
    "/search-user",
    "/supplier-list",
    "/supplier-add",
    "/supplier-detail",
    "/category-list",
    "/category-add",
    "/category-edit",
    "/category-toggle-status",
    "/brand-list",
    "/brand-add",
    "/brand-edit",
    "/brand-toggle-status",
    "/unit-list",
    "/unit-add",
    "/unit-edit",
    "/product-list",
    "/product-add",
    "/product-edit",
    "/purchase-order/list",
    "/purchase-order/create",
    "/purchase-order/edit",
    "/purchase-order/view",
    "/purchase-order/claim",
    "/purchase-order/update-status"
})
public class PermissionFilter implements Filter {

    private static final String VIEW_GOODS_RECEIPT = "view goods receipt";
    private static final String CREATE_GOODS_RECEIPT = "create goods receipt";
    private static final String EDIT_GOODS_RECEIPT = "edit goods receipt";
    private static final String APPROVE_GOODS_RECEIPT = "approve goods receipt";

    private static final String CREATE_GOODS_ISSUE = "create goods issue";
    private static final String VIEW_GOODS_ISSUE = "view goods issue";
    private static final String EDIT_GOODS_ISSUE = "edit goods issue";
    private static final String APPROVE_GOODS_ISSUE = "approve goods issue";

    private static final String VIEW_INVENTORY = "view inventory";
    private static final String VIEW_SUPPLIER = "view supplier";
    private static final String CREATE_SUPPLIER = "create supplier";
    private static final String EDIT_SUPPLIER = "edit supplier";
    private static final String DEACTIVATE_SUPPLIER = "deactivate supplier";
    private static final String VIEW_CATEGORY = "view category";
    private static final String VIEW_BRAND = "view brand";
    private static final String CREATE_BRAND = "create brand";
    private static final String EDIT_BRAND = "edit brand";
    private static final String DEACTIVATE_BRAND = "deactivate brand";

    private static final String VIEW_UNIT = "view unit";
    private static final String CREATE_UNIT = "create unit";
    private static final String EDIT_UNIT = "edit unit";
    private static final String DELETE_UNIT = "delete unit";

    private static final String VIEW_PRODUCT = "view product";
    private static final String CREATE_PRODUCT = "create product";
    private static final String EDIT_PRODUCT = "edit product";
    private static final String DEACTIVATE_PRODUCT = "deactivate product";

    private static final String VIEW_PURCHASE_ORDER = "view purchase order";
    private static final String CREATE_PURCHASE_ORDER = "create purchase order";
    private static final String EDIT_PURCHASE_ORDER = "edit purchase order";
    private static final String CANCEL_PURCHASE_ORDER = "cancel purchase order";
    private static final String CLAIM_PURCHASE_ORDER = "claim purchase order";

    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        Set<String> userPermissions = getUserPermissions(session, user);

        String contextPath = httpRequest.getContextPath();
        String path = httpRequest.getRequestURI().substring(contextPath.length());
        String method = httpRequest.getMethod();

        boolean allowed = checkPermission(path, method, httpRequest, userPermissions);
        if (!allowed) {
            String fallbackPath = resolveFallbackPath(user, userPermissions);
            if (fallbackPath != null && !fallbackPath.equals(path)) {
                httpResponse.sendRedirect(contextPath + fallbackPath + "?denied=true");
                return;
            }
            httpResponse.sendRedirect(contextPath + "/login?denied=true");
            return;
        }

        chain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getUserPermissions(HttpSession session, User user) {
        List<String> permissionNames = roleDAO.getRolePermissionNames(user.getRoleId());
        Set<String> loadedPermissions = new HashSet<>(permissionNames);
        session.setAttribute("userPermissions", loadedPermissions);
        return loadedPermissions;
    }

    private boolean checkPermission(String path, String method, HttpServletRequest request, Set<String> permissions) {
        if ("/goods-receipt-add".equals(path)) {
            return hasPermission(permissions, CREATE_GOODS_RECEIPT);
        }

        if ("/goods-issue-add".equals(path)) {
            return hasPermission(permissions, CREATE_GOODS_ISSUE);
        }

        if ("/goods-receipt-list".equals(path)) {
            if (!hasPermission(permissions, VIEW_GOODS_RECEIPT)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                String id = request.getParameter("id");
                String status = request.getParameter("status");
                if (id != null && status != null) {
                    return hasPermission(permissions, APPROVE_GOODS_RECEIPT);
                }
            }
        }

        if ("/goods-issue-list".equals(path)) {
            if (!hasPermission(permissions, VIEW_GOODS_ISSUE)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                String id = request.getParameter("id");
                String status = request.getParameter("status");
                if (id != null && status != null) {
                    return hasPermission(permissions, APPROVE_GOODS_ISSUE);
                }
            }
        }

        if ("/goods-receipt-edit".equals(path)) {
            if (!hasPermission(permissions, VIEW_GOODS_RECEIPT)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                if (request.getParameter("status") != null) {
                    return hasPermission(permissions, APPROVE_GOODS_RECEIPT);
                }
                return hasPermission(permissions, EDIT_GOODS_RECEIPT);
            }
            return true;
        }

        if ("/goods-issue-detail".equals(path)) {
            if (!hasPermission(permissions, VIEW_GOODS_ISSUE)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method) && request.getParameter("status") != null) {
                return hasPermission(permissions, APPROVE_GOODS_ISSUE);
            }
            return true;
        }

        if (isInventoryPath(path)) {
            return hasPermission(permissions, VIEW_INVENTORY);
        }

        if ("/supplier-list".equals(path)) {
            if (!hasPermission(permissions, VIEW_SUPPLIER)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                String id = request.getParameter("id");
                String status = request.getParameter("status");
                if (id != null && !id.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
                    return hasPermission(permissions, DEACTIVATE_SUPPLIER);
                }
            }
            return true;
        }

        if ("/supplier-add".equals(path)) {
            return hasPermission(permissions, CREATE_SUPPLIER);
        }

        if ("/supplier-detail".equals(path)) {
            if (!hasPermission(permissions, VIEW_SUPPLIER)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                return hasPermission(permissions, EDIT_SUPPLIER);
            }
            return true;
        }

        if (isCategoryPath(path)) {
            return hasPermission(permissions, VIEW_CATEGORY);
        }

        if ("/brand-list".equals(path)) {
            return hasPermission(permissions, VIEW_BRAND);
        }

        if ("/brand-add".equals(path)) {
            return hasPermission(permissions, CREATE_BRAND);
        }

        if ("/brand-edit".equals(path)) {
            if (!hasPermission(permissions, VIEW_BRAND)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                return hasPermission(permissions, EDIT_BRAND);
            }
            return true;
        }

        if ("/brand-toggle-status".equals(path)) {
            return hasPermission(permissions, DEACTIVATE_BRAND);
        }

        if ("/unit-list".equals(path)) {
            if (!hasPermission(permissions, VIEW_UNIT)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method) && request.getParameter("deleteId") != null) {
                return hasPermission(permissions, DELETE_UNIT);
            }
            return true;
        }

        if ("/unit-add".equals(path)) {
            return hasPermission(permissions, CREATE_UNIT);
        }

        if ("/unit-edit".equals(path)) {
            if (!hasPermission(permissions, VIEW_UNIT)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                return hasPermission(permissions, EDIT_UNIT);
            }
            return true;
        }

        if ("/product-list".equals(path)) {
            if (!hasPermission(permissions, VIEW_PRODUCT)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                return hasPermission(permissions, DEACTIVATE_PRODUCT);
            }
            return true;
        }

        if ("/product-add".equals(path)) {
            return hasPermission(permissions, CREATE_PRODUCT);
        }

        if ("/product-edit".equals(path)) {
            if (!hasPermission(permissions, VIEW_PRODUCT)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                return hasPermission(permissions, EDIT_PRODUCT);
            }
            return true;
        }

        if ("/purchase-order/list".equals(path) || "/purchase-order/view".equals(path)) {
            if (!hasPermission(permissions, VIEW_PURCHASE_ORDER)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method) && "cancel".equals(request.getParameter("action"))) {
                return hasPermission(permissions, CANCEL_PURCHASE_ORDER);
            }
            return true;
        }

        if ("/purchase-order/create".equals(path)) {
            return hasPermission(permissions, CREATE_PURCHASE_ORDER);
        }

        if ("/purchase-order/edit".equals(path)) {
            if (!hasPermission(permissions, VIEW_PURCHASE_ORDER)) {
                return false;
            }
            if ("POST".equalsIgnoreCase(method)) {
                return hasPermission(permissions, EDIT_PURCHASE_ORDER);
            }
            return true;
        }

        if ("/purchase-order/claim".equals(path)) {
            return hasPermission(permissions, CLAIM_PURCHASE_ORDER);
        }

        if ("/purchase-order/update-status".equals(path)) {
            return hasAnyPermission(permissions, CANCEL_PURCHASE_ORDER, CLAIM_PURCHASE_ORDER);
        }

        return true;
    }

    private boolean isInventoryPath(String path) {
        return "/inventory-list".equals(path)
                || "/inventory-detail".equals(path)
                || "/inventory-alert".equals(path)
                || "/inventory-transactions".equals(path)
                || "/transaction-detail".equals(path)
                || "/inventory-sheet-list".equals(path)
                || "/inventory-sheet-create".equals(path)
                || "/inventory-sheet-view".equals(path)
                || "/inventory-sheet-edit".equals(path)
                || "/inventory-sheet-approve".equals(path)
                || "/export-inventory".equals(path)
                || "/search-user".equals(path);
    }

    private boolean isCategoryPath(String path) {
        return "/category-list".equals(path)
                || "/category-add".equals(path)
                || "/category-edit".equals(path)
                || "/category-toggle-status".equals(path);
    }

    private boolean hasPermission(Set<String> permissions, String required) {
        return permissions != null && permissions.contains(required);
    }

    private boolean hasAnyPermission(Set<String> permissions, String p1, String p2) {
        return hasPermission(permissions, p1) || hasPermission(permissions, p2);
    }

    private String resolveFallbackPath(User user, Set<String> permissions) {
        if (user == null || user.getRole() == null || user.getRole().getRoleName() == null) {
            return null;
        }

        String roleName = user.getRole().getRoleName().toLowerCase();
        if ("admin".equals(roleName)) {
            return "/user-list";
        }
        if ("sale".equals(roleName)) {
            return "/sales-return-list";
        }
        if (permissions == null || permissions.isEmpty()) {
            return "/login";
        }
        if (permissions.contains(VIEW_INVENTORY)) {
            return "/inventory-list";
        }
        if (permissions.contains(VIEW_SUPPLIER)) {
            return "/supplier-list";
        }
        if (permissions.contains(VIEW_CATEGORY)) {
            return "/category-list";
        }
        if (permissions.contains(VIEW_BRAND)) {
            return "/brand-list";
        }
        if (permissions.contains(VIEW_UNIT)) {
            return "/unit-list";
        }
        if (permissions.contains(VIEW_PRODUCT)) {
            return "/product-list";
        }
        if (permissions.contains(VIEW_PURCHASE_ORDER)) {
            return "/purchase-order/list";
        }
        if (permissions.contains(VIEW_GOODS_RECEIPT)) {
            return "/goods-receipt-list";
        }
        if (permissions.contains(VIEW_GOODS_ISSUE)) {
            return "/goods-issue-list";
        }
        return "/login";
    }
}
