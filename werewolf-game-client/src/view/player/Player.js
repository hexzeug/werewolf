import { useTranslation } from 'react-i18next';
import { ReactComponent as Heart } from './heart.svg';
import './Player.css';
import { useState } from 'react';

const Player = () => {
  const { t } = useTranslation();
  const [marked, setMarked] = useState(false);
  const [disabled, setDisabled] = useState(false);
  window.setDisabled = setDisabled;
  const tags = ['Sishidayako', 'L3ifCraft', 'Ikree'];
  return (
    <button
      className="Player"
      data-marked={marked}
      data-player-state={'sleeping'}
      disabled={disabled}
      onClick={() => {
        setMarked(!marked);
      }}
    >
      <PlayerSnore>{t('player.snore_symbol')}</PlayerSnore>
      <PlayerTags tags={tags} />
      <div className="Player__body">
        <Heart className="Player__heart" alt={t('player.alt.in_love')} />
        <span className="Player__section-sign">§</span>
        <span className="Player__name">hexszeug</span>
        <span className="Player__role">{t('roles.werewolf')}</span>
      </div>
    </button>
  );
};

const PlayerTags = ({ tags }) => {
  return (
    <div className="Player__tags">
      {tags.map((tag) => <span className="Player__tag">{tag}</span>).reverse()}
    </div>
  );
};

const PlayerSnore = ({ children }) => {
  return (
    <div className="Player__snore">
      <span>{children}</span>
      <span>{children}</span>
      <span>{children}</span>
    </div>
  );
};

export default Player;
