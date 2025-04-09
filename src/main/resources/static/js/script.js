// Handle login form submission
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                username: username,
                password: password
            }),
            credentials: 'include' // Include cookies for session management
        });

        if (response.ok) {
            window.location.href = '/welcome.html'; // Redirect to welcome page
        } else {
            document.getElementById('error').textContent = 'Invalid credentials';
        }
    } catch (error) {
        document.getElementById('error').textContent = 'An error occurred';
        console.error(error);
    }
});

// Handle logout
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
    try {
        const response = await fetch('http://localhost:8080/logout', {
            method: 'POST',
            credentials: 'include' // Include cookies for session management
        });

        if (response.ok) {
            window.location.href = '/index.html'; // Redirect to login page
        }
    } catch (error) {
        console.error('Logout failed:', error);
    }
});

// document.getElementById('loginForm').addEventListener('submit', function(event) {
//     event.preventDefault(); // Prevent default form submission for demo (optional)
//     this.submit(); // Submit the form to Spring Security's /login
// });