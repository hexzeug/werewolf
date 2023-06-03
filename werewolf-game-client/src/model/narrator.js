import { useEffect, useRef, useState } from 'react';

const model = {
  hooks: new Set(),
  narration: {
    text: 'narrator.generic.sunset',
  },
};

export const narrate = async (text, data) => {
  const promises = [];
  model.hooks.forEach((hook) => {
    promises.push(animate(hook, { text, data }));
  });
  await Promise.all(promises);
};

const animate = async (hook, narration) => {
  try {
    await hook.ref.current?.animate(
      [{ opacity: 1 }, { opacity: 0 }],
      hook.fadeOut * 1000 // s to ms
    ).finished;
  } catch (e) {
    // animation was canceled
    if (!(e instanceof DOMException)) throw e;
  }
  hook.setNarration(narration);
  try {
    await hook.ref.current?.animate(
      [{ opacity: 0 }, { opacity: 1 }],
      hook.fadeIn * 1000 // s to ms
    ).finished;
  } catch (e) {
    // animation was canceled
    if (!(e instanceof DOMException)) throw e;
  }
};

export const useNarrator = (fadeOut, fadeIn) => {
  const [narration, setNarration] = useState(model.narration);
  const ref = useRef(null);
  useEffect(() => {
    model.hooks.add({ ref, setNarration, fadeOut, fadeIn });
    return () => model.hooks.delete({ ref, setNarration, fadeOut, fadeIn });
  }, [fadeOut, fadeIn]);
  return [narration, ref];
};
