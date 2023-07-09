const path = window.location.pathname;
export const inRoom = path.endsWith('/room') || path.endsWith('/room/');
export const base_url = inRoom
  ? path.slice(0, path.lastIndexOf('/room') + 1)
  : path.endsWith('/')
  ? path
  : path + '/';

const request = async (url, method) => {
  const res = await fetch(base_url + 'api/alpha/launcher' + url, { method });
  const { ok, status } = res;
  try {
    return { body: await res.clone().json(), ok, status };
  } catch {
    return { body: await res.text(), ok, status };
  }
};

const api = {
  get: (url) => request(url, 'GET'),
  post: (url) => request(url, 'POST'),
};

export default api;
