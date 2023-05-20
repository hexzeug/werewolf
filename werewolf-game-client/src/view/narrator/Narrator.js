import './Narrator.css';
import { useNarrator } from '../../model/narrator';

const Narrator = () => {
  const [text, ref] = useNarrator(0.5, 1);
  return (
    <div className="Narrator">
      <h1 className="Narrator__text" ref={ref}>
        {text}
      </h1>
    </div>
  );
};

export default Narrator;
