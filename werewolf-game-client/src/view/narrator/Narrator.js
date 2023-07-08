import './Narrator.css';
import { useNarrator } from '../../model/narrator';
import { useTranslation } from 'react-i18next';

const Narrator = () => {
  const { t } = useTranslation();
  const [narration, ref] = useNarrator(0.5, 1, 3);
  return (
    <div className="Narrator">
      <h1 className="Narrator__text" ref={ref}>
        {narration && t(narration.text, narration.data)}
      </h1>
    </div>
  );
};

export default Narrator;
