import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import GameCreator from './GameCreator';
import GameCreated from './GameCreated';
import Feedback from './Feedback';

function HomeApp() {
  const { t } = useTranslation();
  const [tokens, setTokens] = useState(null);

  return (
    <main>
      <section className="section">
        {!tokens && <GameCreator setTokens={setTokens} />}
        {tokens && <GameCreated tokens={tokens} />}
      </section>
      <section className="section">
        <div className="container">
          <div className="content">
            <h1>{t('home.about')}</h1>
            <p>
              Dies ist die Alpha von Werewolf, einem zugbasiertem online
              multiplayer Game. <br />
              Ich bin Schüler und habe diese Version in etwa 3 Monaten neben der
              Schule alleine entwickelt.
            </p>
            <p>
              Das Spiel befindet sich noch sehr früh in der Entwicklung,
              trotzdem kann man es jetzt schon testen. Die bisherige Entwichlung
              hat sich hauptsächlich auf die Spielelogik konzentriert. Design
              war erst einmal zweitrangig. Außerdem können sich natürlich auch
              noch der eine oder andere größere Bug im Spiel verstecken, und es
              gibt noch keine Stats, Accounts, o.ä. <br />
              Es gibt auch noch keine ausführliche Anleitung, das Spiel ist aber
              sehr intuitiv spielbar, vorallem wenn man die Werwölfe von
              Düsterwald kennt.
            </p>
            <p>Viel Spaß beim Spielen!</p>
          </div>
        </div>
      </section>
      <section className="section">
        <div className="container">
          <div className="content">
            <h1>{t('home.feedback')}</h1>
            <p>{t('home.feedback.info')}</p>
            <p>{t('home.feedback.disclaimer')}</p>
            <Feedback />
          </div>
        </div>
      </section>
    </main>
  );
}

export default HomeApp;
