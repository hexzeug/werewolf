import { useEffect, useRef, useState } from 'react';
import { Mutex } from 'async-mutex';
import { sleep } from '../utils';

// Constants
const FADE_OUT_MS = 500;
const FADE_IN_MS = 1000;
const COOLDOWN_MS = 2500;

// Storage

/*
narration: {
  text?: String, (translate key)
  data?: Object, (translate interpolation data)
}
*/
const store = {
  narration: {},
};

// Trigger

const mutex = new Mutex();

export const narrate = async (text, data) => {
  const release = await mutex.acquire();
  setTimeout(release, COOLDOWN_MS);
  hooks.forEach((hook) => {
    animate(hook, { text, data });
  });
  await sleep(FADE_OUT_MS + FADE_IN_MS);
};

window.narrate = narrate;

const animate = async (hook, narration) => {
  const fadeOut = hook.ref.current?.animate(
    [{ opacity: 1 }, { opacity: 0 }],
    FADE_OUT_MS
  );
  await Promise.race([sleep(FADE_OUT_MS), fadeOut.finished]);
  hook.setNarration(narration);
  const fadeIn = hook.ref.current?.animate(
    [{ opacity: 0 }, { opacity: 1 }],
    FADE_IN_MS
  );
  await Promise.race([sleep(FADE_IN_MS), fadeIn.finished]);
};

// Subscription

const hooks = new Set();

export const useNarrator = () => {
  const [narration, setNarration] = useState(store.narration);
  const ref = useRef(null);
  useEffect(() => {
    const hook = {
      ref,
      setNarration,
    };
    hooks.add(hook);
    return () => hooks.delete(hook);
  }, []);
  return [narration, ref];
};
