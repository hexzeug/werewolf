import { useEffect, useRef, useState } from 'react';

// Storage

/*
narration: {
  text?: String, (translate key)
  data?: Object, (translate interpolation data)
}
*/
const storage = {
  narration: {},
};

// Trigger

export const narrate = async (text, data) => {
  const promises = [];
  hooks.forEach((hook) => {
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

// Subscription

const hooks = new Set();

export const useNarrator = (fadeOut, fadeIn) => {
  const [narration, setNarration] = useState(storage.narration);
  const ref = useRef(null);
  useEffect(() => {
    hooks.add({ ref, setNarration, fadeOut, fadeIn });
    return () => hooks.delete({ ref, setNarration, fadeOut, fadeIn });
  }, [fadeOut, fadeIn]);
  return [narration, ref];
};
