import { useTranslation } from 'react-i18next';
import { base_url } from '../api';
import { useRef, useState } from 'react';

const base_link = window.location.origin + base_url + 'room?wat=';

const RoomLink = ({ index, token }) => {
  const { t } = useTranslation();
  const input = useRef(null);
  const [tooltip, setTooltip] = useState(null);

  const handleClick = async () => {
    input.current.select();
    input.current.setSelectionRange(0, input.current.value.length);
    try {
      await navigator.clipboard.writeText(input.current.value);
      setTooltip('home.link.tooltip.copied');
    } catch {
      setTooltip('home.link.tooltip.cannot_copy');
    }
  };
  return (
    <div className="field has-addons" key={token}>
      <div className="control">
        <button className="button is-static is-small">
          {t('home.game.player')} {index + 1}
        </button>
      </div>
      <div className="control is-expanded">
        <input
          className="input is-small"
          type="text"
          ref={input}
          value={base_link + token}
          readOnly
        />
      </div>
      <div className="control">
        <button
          className="button is-small has-tooltip-arrow"
          onClick={handleClick}
          data-tooltip={tooltip ? t(tooltip) : null}
          onMouseLeave={() => setTooltip(null)}
        >
          {t('home.game.copy')}
        </button>
      </div>
    </div>
  );
};

export default RoomLink;
