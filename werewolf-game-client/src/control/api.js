const request = async (url, method, body, options) => {
  const res = await fetch('/api' + url, {
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
  } catch (ex) {
    if (!(ex instanceof SyntaxError)) throw ex;
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
