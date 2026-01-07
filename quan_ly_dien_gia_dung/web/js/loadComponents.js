/**
 * Load HTML components into the page
 */
function loadComponent(elementId, componentPath) {
    return fetch(componentPath)
        .then(response => response.text())
        .then(data => {
            document.getElementById(elementId).innerHTML = data;
        })
        .catch(error => console.error('Error loading component:', error));
}

// Load components when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Load both components
    const sidebarPromise = document.getElementById('sidebar-container') 
        ? loadComponent('sidebar-container', 'components/sidebar.jsp')
        : Promise.resolve();
    
    const navbarPromise = document.getElementById('navbar-container')
        ? loadComponent('navbar-container', 'components/navbar.jsp')
        : Promise.resolve();
    
    // Wait for both components to load, then initialize sidebar toggler
    Promise.all([sidebarPromise, navbarPromise]).then(() => {
        // Re-initialize sidebar toggler after components are loaded
        if (typeof $ !== 'undefined') {
            $('.sidebar-toggler').off('click').on('click', function () {
                $('.sidebar, .content').toggleClass("open");
                return false;
            });
        }
    });
});

