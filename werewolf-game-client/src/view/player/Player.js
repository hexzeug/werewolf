import { useTranslation } from 'react-i18next';
import { ReactComponent as Heart } from './heart.svg';
import './Player.css';

/* 
player: {
  name: String,
  status: String,
  role: String | null,
  marked: boolean,
  inLove: boolean,
  accused: boolean,
  playerTags: Array[String(playername)],
  disabled: boolean,
  onClick: Function | null,
}
 */

const playerTemplate = {
  name: 'hexszeug',
  status: 'sleeping',
  role: 'werewolf',
  marked: true,
  inLove: true,
  accused: true,
  playerTags: ['Sishidayako', 'L3ifCraft', 'Ikree'],
  disabled: false,
  onClick: () => {
    alert('you clicked on a player');
  },
};

const Player = ({ player }) => {
  const { t } = useTranslation();
  player = playerTemplate;
  return (
    <div
      className="Player"
      data-marked={player.marked}
      data-dead={player.status === 'dead'}
    >
      {player.status === 'sleeping' && <PlayerSnore />}
      <PlayerTags tags={player.playerTags} />

      <button
        className={`Player__body ${player.onClick ? 'button' : ''}`}
        disabled={player.disabled}
        onClick={player.onClick}
      >
        {player.inLove && (
          <Heart className="Player__heart" alt={t('player.alt.in_love')} />
        )}
        {player.accused && <span className="Player__section-sign">§</span>}

        <span className="Player__name">{player.name}</span>
        {player.role !== undefined && (
          <span className="Player__role">{t(`roles.${player.role}`)}</span>
        )}
      </button>
    </div>
  );
};

const PlayerTags = ({ tags }) => {
  return (
    <div className="Player__tags">
      {tags
        .map((tag) => (
          <span className="Player__tag" key={tag}>
            {tag}
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
      <span>{zzz[1]}</span>
    </div>
  );
};

export default Player;
