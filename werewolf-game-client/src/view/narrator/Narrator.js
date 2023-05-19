import './Narrator.css';
import { useNarrator } from '../../model/narrator';

const Narrator = () => {
  const [text, ref] = useNarrator();
  return (
    <p className="Narrator" ref={ref}>
      {text}
    </p>
  );
};

export default Narrator;
