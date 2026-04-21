document.addEventListener('DOMContentLoaded', () => {
    // 1. Inject Styles
    const style = document.createElement('style');
    style.innerHTML = `
        #globalfetchLoader {
            display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0, 0, 0, 0.7); z-index: 999999; justify-content: center;
            align-items: center; flex-direction: column; color: #fff;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; backdrop-filter: blur(5px);
        }
        .fetch-spinner {
            width: 60px; height: 60px; border: 6px solid rgba(255, 255, 255, 0.2);
            border-radius: 50%; border-top-color: #4facfe; animation: fetchSpin 1s linear infinite;
            margin-bottom: 20px;
        }
        @keyframes fetchSpin { to { transform: rotate(360deg); } }
        @keyframes fetchPulse { 0%,100% { opacity: 0.6; } 50% { opacity: 1; } }
    `;
    document.head.appendChild(style);

    // 2. Inject HTML Overlay
    const loaderDiv = document.createElement('div');
    loaderDiv.id = 'globalfetchLoader';
    loaderDiv.innerHTML = `
        <div class="fetch-spinner"></div>
        <h3 style="margin-top: 10px; font-weight: 400; letter-spacing: 2px; animation: fetchPulse 1.5s infinite;">Loading Data...</h3>
    `;
    document.body.appendChild(loaderDiv);

    // 3. Intercept Fetch API
    const originalFetch = window.fetch;
    let activeRequests = 0;

    window.fetch = async function(...args) {
        // Exclude /auth/login as it has its own specialized loader
        const url = typeof args[0] === 'string' ? args[0] : (args[0] ? args[0].url : '');
        const isAuth = url.includes('/auth/login');
        
        if (!isAuth) {
            activeRequests++;
            const loader = document.getElementById('globalfetchLoader');
            if (loader) loader.style.display = 'flex';
        }

        try {
            const response = await originalFetch.apply(this, args);
            return response;
        } finally {
            if (!isAuth) {
                activeRequests--;
                if (activeRequests <= 0) {
                    activeRequests = 0;
                    const loader = document.getElementById('globalfetchLoader');
                    if(loader) loader.style.display = 'none';
                }
            }
        }
    };
});
