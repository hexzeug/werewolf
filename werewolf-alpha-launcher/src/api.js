const path = window.location.pathname;
export const inRoom = path.endsWith('/room') || path.endsWith('/room/');
export const base_url = inRoom
  ? path.slice(0, path.lastIndexOf('/room') + 1)
  : path.endsWith('/')
  ? path
  : path + '/';

export const setWat = (wat) => {
  document.cookie = `wat=${wat}; path=${base_url}`;
};

export const redirect_url = base_url + 'play/';

export const startPolling = () => {
  setInterval(async () => {
    const { ok, body } = await api.get('/running');
    if (ok && body === true) window.location.pathname = redirect_url;
  }, 500);
};

const request = async (url, method, body) => {
  const res = await fetch(
    base_url + 'api/alpha/launcher' + url,
    body
      ? {
          method,
          body,
        }
      : { method }
  );
  const { ok, status } = res;
  try {
    return { body: await res.clone().json(), ok, status };
  } catch {
    return { body: await res.text(), ok, status };
  }
};

const api = {
  get: (url) => request(url, 'GET', null),
  post: (url, body) => request(url, 'POST', body),
};

export default api;
