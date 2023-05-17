import { useTranslation } from 'react-i18next';
import { ReactComponent as Heart } from './heart.svg';
import './Player.css';
import { useState } from 'react';

const Player = () => {
  const { t } = useTranslation();
  const [marked, setMarked] = useState(false);
  const [disabled, setDisabled] = useState(false);
  return (
    <button
      className="Player"
      data-marked={marked}
      data-player-state={'awake'}
      disabled={disabled}
      onClick={() => {
        setMarked(!marked);
      }}
      onContextMenu={(e) => {
        e.preventDefault();
        setDisabled(!disabled);
      }}
    >
      <div className="Player__snore">
        <span>{t('player.snore_symbol')}</span>
        <span>{t('player.snore_symbol')}</span>
        <span>{t('player.snore_symbol')}</span>
      </div>
      <div className="Player__body">
        <Heart className="heart" alt={t('player.alt.in_love')} />
        <span className="section-sign">§</span>
        <span className="name text">hexszeug</span>
        <span className="role text">{t('roles.werewolf')}</span>
      </div>
    </button>
  );
};

export default Player;
