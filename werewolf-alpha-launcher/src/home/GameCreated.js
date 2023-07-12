import { useTranslation } from 'react-i18next';
import RoomLink from './RoomLink';
import GameJoiner from '../room/GameJoiner';

const GameCreated = ({ tokens }) => {
  const { t } = useTranslation();

  return (
    <div className="container">
      <div className="block has-text-centered">
        <h1 className="title">{t('home.game.title')}</h1>
      </div>
      <div className="columns is-centered">
        <div className="column is-half">
          <div className="block">
            <p>{t('home.game.info.others')}</p>
            <p>{t('home.game.info.disclaimer')}</p>
          </div>
          <div className="block">
            {tokens.slice(1).map((token, index) => (
              <RoomLink key={token} index={index} token={token} />
            ))}
          </div>
          <div className="block">
            <p>{t('home.game.info.self')}</p>
          </div>
          <div className="block">
            <GameJoiner token={tokens[0]} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameCreated;
