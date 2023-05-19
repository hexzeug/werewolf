import { useEffect, useRef, useState } from 'react';

const model = {
  hooks: new Set(),
};

export const narrate = async (text) => {
  const blendoffs = [];
  model.hooks.forEach(({ ref }) =>
    blendoffs.push(
      ref.current.animate([{ opacity: 1 }, { opacity: 0 }], 1000).finished
    )
  );
  await Promise.all(blendoffs);
  model.hooks.forEach(({ setText }) => setText(text));
  const blendins = [];
  model.hooks.forEach(({ ref }) =>
    blendins.push(
      ref.current.animate([{ opacity: 0 }, { opacity: 1 }], 1000).finished
    )
  );
  await Promise.all(blendins);
};
window.narrate = narrate;

export const useNarrator = () => {
  const [text, setText] = useState(null);
  const ref = useRef(null);
  useEffect(() => {
    model.hooks.add({ ref, setText });
    return () => model.hooks.delete({ ref, setText });
  }, []);
  return [text, ref];
};
