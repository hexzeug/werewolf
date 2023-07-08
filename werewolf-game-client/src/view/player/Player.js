import { useTranslation } from 'react-i18next';
import { usePlayer } from '../../model/player';
import { ReactComponent as Heart } from './heart.svg';
import './Player.css';
import Spinner from '../spinner/Spinner';
import { useCallback } from 'react';
import { cache } from '../../control/logic';

const Player = ({ playerId }) => {
  const { t } = useTranslation();
  const player = usePlayer(playerId);
  const {
    name,
    status,
    role,
    marked,
    inLove,
    accused,
    playerTags,
    disabled,
    onClick,
  } = player ?? {};
  const domOnClick = useCallback(() => onClick(playerId), [onClick, playerId]);
  const isDead = status === 'dead';
  const isButton = (onClick || disabled) && !isDead;
  const bodyContent = (
    <>
      {inLove && (
        <Heart className="Player__heart" title={t('player.alt.inLove')} />
      )}
      {accused && <span className="Player__section-sign">§</span>}
      <span className="Player__name">{name}</span>
      {!player && <Spinner />}
      {role && <span className="Player__role">{t(`roles.${role}`)}</span>}
    </>
  );
  return (
    <div className="Player" data-marked={marked || null}>
      {status === 'sleeping' && <PlayerSnore />}
      {playerTags && (
        <div className="Player__tags">
          {playerTags.map((playerId) => (
            <PlayerTag key={playerId} playerId={playerId} />
          ))}
        </div>
      )}
      {isButton ? (
        <button
          className="Player__body button"
          disabled={disabled}
          onClick={onClick && domOnClick}
          data-dead={isDead || null}
        >
          {bodyContent}
        </button>
      ) : (
        <div className="Player__body" data-dead={isDead || null}>
          {bodyContent}
        </div>
      )}
    </div>
  );
};

const PlayerTag = ({ playerId }) => {
  const { t } = useTranslation();
  let { name } = usePlayer(playerId);
  if (playerId === cache.ownId) name = t('player.tags.you');
  return <span className="Player__tag">{name}</span>;
};

const PlayerSnore = () => {
  const { t } = useTranslation();
  const zzz = t('player.snoreSymbol3Char');
  return (
    <div className="Player__snore" data-testid="snore">
      <span>{zzz[0]}</span>
      <span>{zzz[1]}</span>
      <span>{zzz[2]}</span>
    </div>
  );
};

export default Player;
