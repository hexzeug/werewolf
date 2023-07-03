const request = async (url, method, body, options) => {
  const res = await fetch('/api' + url, {
    ...(body && { body: JSON.stringify(body) }),
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
    resBody = await res.json();
  } catch (ex) {
    if (!(ex instanceof SyntaxError)) throw ex;
    resBody = await res.text();
  }
  return {
    ok,
    status,
    headers,
    body: resBody,
  };
};

const api = {
  get: (url, options = {}) => {
    return request(url, 'GET', null, options);
  },
  post: (url, body, options = {}) => {
    return request(url, 'POST', body, options);
  },
  put: (url, body, options = {}) => {
    return request(url, 'PUT', body, options);
  },
  delete: (url, options = {}) => {
    return request(url, 'DELTE', null, options);
  },
};

export default api;
