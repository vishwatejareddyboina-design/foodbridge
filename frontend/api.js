// api.js
// Handles communication with the PHP Backend

const API_BASE_URL = 'http://localhost/backend/api';

/**
 * Make an API POST request
 */
async function apiCall(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        return { status: 'error', message: 'Network error occurred.' };
    }
}

/**
 * Session Management
 */
const SessionManager = {
    login: (userData) => {
        localStorage.setItem('currentUser', JSON.stringify(userData));
    },
    logout: () => {
        localStorage.removeItem('currentUser');
        window.location.href = 'index.html'; // Or choose_role.html
    },
    getUser: () => {
        const user = localStorage.getItem('currentUser');
        return user ? JSON.parse(user) : null;
    },
    isLoggedIn: () => {
        return localStorage.getItem('currentUser') !== null;
    }
};

/**
 * UI Helpers
 */
function showToast(message, isError = false) {
    // A simple alert for now, could be upgraded to a nice toast UI
    alert(message);
}

function loadUserDataIntoUI() {
    const user = SessionManager.getUser();
    if (!user) return;

    // 1. Update Name on Dashboard header
    const nameElements = document.querySelectorAll('.dashboard-user-name');
    nameElements.forEach(el => el.textContent = user.name);

    // 2. Update Name & Email in Profile/Settings screen
    const profileNameEl = document.getElementById('profile-name');
    const profileEmailEl = document.getElementById('profile-email');
    
    if (profileNameEl) profileNameEl.textContent = user.name;
    if (profileEmailEl) profileEmailEl.textContent = user.email;
}

// Automatically load user data when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    loadUserDataIntoUI();
});
