// absolute path without the optional suffix 'play/'
export const base_url = window.location.pathname.match(/^(.*?)(?:play\/)?$/)[1];
export const api_url = base_url + 'api';

const request = async (url, method, body, options) => {
  const res = await fetch(api_url + url, {
    ...(body !== undefined && { body: JSON.stringify(body) }),
    method,
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });
  const { status, headers, ok } = res;
  let resBody;
  try {
    resBody = await res.clone().json();
  } catch {
    resBody = await res.text();
  }
  return {
    url,
    ok,
    status,
    headers,
    body: resBody,
  };
};

const api = {
  get: (url, options = {}) => {
    return request(url, 'GET', undefined, options);
  },
  post: (url, body, options = {}) => {
    return request(url, 'POST', body, options);
  },
  put: (url, body, options = {}) => {
    return request(url, 'PUT', body, options);
  },
  delete: (url, options = {}) => {
    return request(url, 'DELETE', undefined, options);
  },
};

export const bodyIfOk = async (requestPromise) => {
  const { url, body, ok, status } = await requestPromise;
  if (!ok) {
    throw new Error(`Request to ${url} failed with status code ${status}.`);
  }
  return body;
};

export default api;
