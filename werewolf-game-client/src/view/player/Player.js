import { useTranslation } from 'react-i18next';
import { ReactComponent as Heart } from './heart.svg';
import './Player.css';
import { useState } from 'react';

const Player = () => {
  const { t } = useTranslation();
  const [marked, setMarked] = useState(false);
  const [disabled, setDisabled] = useState(false);
  window.setDisabled = setDisabled;
  const z = t('player.snore_symbol');
  return (
    <button
      className="Player"
      data-marked={marked}
      data-player-state={'awake'}
      disabled={disabled}
      onClick={() => {
        setMarked(!marked);
      }}
    >
      <div className="Player__snore">
        <span>{z}</span>
        <span>{z}</span>
        <span>{z}</span>
      </div>
      <div className="Player__body">
        <Heart className="Player__heart" alt={t('player.alt.in_love')} />
        <span className="Player__section-sign">§</span>
        <span className="Player__name">hexszeug</span>
        <span className="Player__role">{t('roles.werewolf')}</span>
      </div>
    </button>
  );
};

export default Player;
