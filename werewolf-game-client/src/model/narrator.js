import { useEffect, useRef, useState } from 'react';
import i18n from '../i18n';

const model = {
  hooks: new Set(),
};

export const narrate = async (text) => {
  const promises = [];
  model.hooks.forEach((hook) => {
    promises.push(animate(hook, text));
  });
  await Promise.all(promises);
};

const animate = async (hook, text) => {
  try {
    await hook.ref.current?.animate(
      [{ opacity: 1 }, { opacity: 0 }],
      hook.fadeOut * 1000
    ).finished;
  } catch (e) {
    // animation was canceled
    if (!(e instanceof DOMException)) throw e;
  }
  hook.setText(text);
  try {
    await hook.ref.current?.animate(
      [{ opacity: 0 }, { opacity: 1 }],
      hook.fadeIn * 1000
    ).finished;
  } catch (e) {
    // animation was canceled
    if (!(e instanceof DOMException)) throw e;
  }
};

// debug / development
window.narrate = narrate;
setTimeout(async () => {
  await narrate(i18n.t('narrator.werewolves.awake'));
}, 500);

export const useNarrator = (fadeOut, fadeIn) => {
  const [text, setText] = useState(null);
  const ref = useRef(null);
  useEffect(() => {
    model.hooks.add({ ref, setText, fadeOut, fadeIn });
    return () => model.hooks.delete({ ref, setText, fadeOut, fadeIn });
  }, [fadeOut, fadeIn]);
  return [text, ref];
};
