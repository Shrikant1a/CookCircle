/**
 * CookCircle Central API Client
 * Robust, zero-dependency HTTP client supporting interceptors, JWT authorization,
 * query parameter serialization, JSON parsing, and unified error handling.
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

class ApiClient {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
    this.requestInterceptors = [];
    this.responseInterceptors = [];
  }

  // Add request interceptor
  addRequestInterceptor(fn) {
    this.requestInterceptors.push(fn);
  }

  // Add response interceptor
  addResponseInterceptor(fn, errFn) {
    this.responseInterceptors.push({ success: fn, error: errFn });
  }

  // Core request engine
  async request(endpoint, options = {}) {
    let url = endpoint.startsWith('http') ? endpoint : `${this.baseUrl}${endpoint.startsWith('/') ? '' : '/'}${endpoint}`;
    
    // Process query params
    if (options.params) {
      const queryParams = new URLSearchParams();
      Object.entries(options.params).forEach(([key, val]) => {
        if (val !== undefined && val !== null) {
          queryParams.append(key, val);
        }
      });
      const queryString = queryParams.toString();
      if (queryString) {
        url += (url.includes('?') ? '&' : '?') + queryString;
      }
    }

    // Default headers
    let config = {
      method: options.method || 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        ...(options.headers || {})
      },
      ...options
    };

    // Apply request interceptors
    for (const interceptor of this.requestInterceptors) {
      config = await interceptor(config);
    }

    if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
      config.body = JSON.stringify(config.body);
    }

    try {
      const response = await fetch(url, config);

      let data;
      const contentType = response.headers.get('content-type') || '';
      if (contentType.includes('application/json')) {
        data = await response.json();
      } else if (contentType.includes('text/csv') || contentType.includes('text/plain')) {
        data = await response.text();
      } else {
        data = await response.text();
      }

      const resObj = {
        data,
        status: response.status,
        statusText: response.statusText,
        headers: response.headers,
        ok: response.ok
      };

      if (!response.ok) {
        let errMsg = (data && (data.message || data.error));
        if (!errMsg && response.status >= 500) {
          errMsg = 'Backend service offline. Local client session fallback activated.';
        }
        const error = new Error(
          errMsg || `HTTP ${response.status}: ${response.statusText}`
        );
        error.response = resObj;
        error.status = response.status;
        error.isNetworkError = response.status >= 500;
        
        // Execute error interceptors
        for (const interceptor of this.responseInterceptors) {
          if (interceptor.error) {
            interceptor.error(error);
          }
        }
        throw error;
      }

      // Execute success interceptors
      let finalRes = resObj;
      for (const interceptor of this.responseInterceptors) {
        if (interceptor.success) {
          finalRes = await interceptor.success(finalRes);
        }
      }

      return finalRes.data;
    } catch (err) {
      if (!err.response) {
        const networkError = new Error(err.message || 'Network error: could not connect to server');
        networkError.isNetworkError = true;
        throw networkError;
      }
      throw err;
    }
  }

  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  post(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'POST', body });
  }

  put(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'PUT', body });
  }

  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  }
}

export const api = new ApiClient(BASE_URL);

// Inject JWT Token from LocalStorage
api.addRequestInterceptor(async (config) => {
  const token = localStorage.getItem('cookcircle_token');
  if (token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${token}`
    };
  }
  return config;
});

// Handle 401 Unauthorized
api.addResponseInterceptor(
  (response) => response,
  (error) => {
    if (error.status === 401) {
      console.warn('Session expired or unauthorized. Token cleared.');
      localStorage.removeItem('cookcircle_token');
    }
    return Promise.reject(error);
  }
);

export default api;

