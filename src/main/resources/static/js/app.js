/* ======================================
   API Helper — Centralized API calls
   ====================================== */
const API_BASE = '';

const api = {
    async request(method, url, body = null) {
        const headers = { 'Content-Type': 'application/json' };
        const token = localStorage.getItem('accessToken');
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const options = { method, headers };
        if (body) options.body = JSON.stringify(body);

        const response = await fetch(API_BASE + url, options);
        
        if (response.status === 401) {
            localStorage.clear();
            window.location.href = '/login.html';
            return;
        }

        const data = await response.json();
        return data;
    },

    get(url)       { return this.request('GET', url); },
    post(url, body){ return this.request('POST', url, body); },
    put(url, body) { return this.request('PUT', url, body); },
    del(url)       { return this.request('DELETE', url); },

    async upload(url, formData) {
        const headers = {};
        const token = localStorage.getItem('accessToken');
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const response = await fetch(API_BASE + url, {
            method: 'POST',
            headers,
            body: formData
        });
        return response.json();
    }
};

/* ======================================
   Auth Helpers
   ====================================== */
function getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

function getRole() {
    const user = getUser();
    return user ? user.role : null;
}

function isLoggedIn() {
    return !!localStorage.getItem('accessToken');
}

function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
    }
}

function requireRole(role) {
    requireAuth();
    if (getRole() !== role) {
        window.location.href = '/dashboard.html';
    }
}

/* ======================================
   Toast Notifications
   ====================================== */
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

/* ======================================
   Navbar — Update based on auth state
   ====================================== */
function updateNavbar() {
    const navLinks = document.getElementById('navLinks');
    if (!navLinks) return;

    if (isLoggedIn()) {
        const user = getUser();
        const initial = (user.email || 'U')[0].toUpperCase();
        const roleBadge = user.role === 'STUDENT' ? 'badge-student' :
                          user.role === 'RECRUITER' ? 'badge-recruiter' : 'badge-admin';
        navLinks.innerHTML = `
            <a href="/dashboard.html" class="nav-link">Dashboard</a>
            <div class="nav-user">
                <div class="nav-user-info">
                    <div class="nav-user-avatar">${initial}</div>
                    <span class="nav-role-badge ${roleBadge}">${user.role}</span>
                </div>
                <button class="btn-logout" onclick="logout()">Logout</button>
            </div>
        `;
    }
}

// Run on every page
document.addEventListener('DOMContentLoaded', updateNavbar);
