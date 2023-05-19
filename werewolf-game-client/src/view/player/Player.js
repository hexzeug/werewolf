import { useTranslation } from 'react-i18next';
import { usePlayerMap } from '../../model/player';
import { ReactComponent as Heart } from './heart.svg';
import './Player.css';
import Spinner from '../spinner/Spinner';

/* 
player: {
  name: String,
  status: String,
  role?: String,
  marked?: boolean,
  inLove?: boolean,
  accused?: boolean,
  playerTags?: Array[String(playername)],
  disabled?: boolean,
  onClick?: Function,
}
 */

const Player = ({ playerId }) => {
  const { t } = useTranslation();
  const player = usePlayerMap()[playerId];
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
  const isDead = status === 'dead';
  const isButton = (onClick || disabled) && !isDead;
  return (
    <div className="Player" data-marked={marked || null}>
      {status === 'sleeping' && <PlayerSnore />}
      {playerTags && <PlayerTags tags={playerTags} />}
      <button
        className={`Player__body ${isButton ? 'button' : ''}`}
        disabled={isButton ? disabled : null}
        onClick={isButton ? onClick : null}
        data-dead={isDead || null}
      >
        {inLove && (
          <Heart className="Player__heart" alt={t('player.alt.in_love')} />
        )}
        {accused && <span className="Player__section-sign">§</span>}
        <span className="Player__name">{name}</span>
        {!player && <Spinner />}
        {role && <span className="Player__role">{t(`roles.${role}`)}</span>}
      </button>
    </div>
  );
};

const PlayerTags = ({ tags }) => {
  const playerMap = usePlayerMap();
  return (
    <div className="Player__tags">
      {tags
        .map((playerId) => (
          <span className="Player__tag" key={playerId}>
            {playerMap[playerId].name}
          </span>
        ))
        .reverse()}
    </div>
  );
};

const PlayerSnore = () => {
  const { t } = useTranslation();
  const zzz = t('player.snore_symbol_3_char');
  return (
    <div className="Player__snore">
      <span>{zzz[0]}</span>
      <span>{zzz[1]}</span>
      <span>{zzz[2]}</span>
    </div>
  );
};

export default Player;
