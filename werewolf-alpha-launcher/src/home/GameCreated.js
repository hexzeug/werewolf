import { useTranslation } from 'react-i18next';
import RoomLink from './RoomLink';

const GameCreated = ({ tokens }) => {
  const { t } = useTranslation();

  return (
    <div className="columns is-centered">
      <div className="column is-half">
        <div className="block">
          <h1 className="title has-text-centered">{t('home.game.title')}</h1>
          <p>{t('home.game.info')}</p>
        </div>
        <div className="block">
          {tokens.slice(1).map((token, index) => (
            <RoomLink key={token} index={index} token={token} />
          ))}
        </div>
        <div className="block">
          <div className="field">
            <div className="control">
              <a className="button is-fullwidth" href={tokens[0]}>
                {t('home.game.link')}
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameCreated;
