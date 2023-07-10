import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import RoomLink from './RoomLink';
import GameCreator from './GameCreator';

function HomeApp() {
  const { t } = useTranslation();
  const [tokens, setTokens] = useState(null);

  return (
    <main>
      <section className="section">
        {!tokens && <GameCreator setTokens={setTokens} />}
        {tokens && (
          <div className="columns is-centered">
            <div className="column is-half">
              <div className="block">
                <h1 className="title has-text-centered">
                  {t('home.game.title')}
                </h1>
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
        )}
      </section>
      <section className="section">
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure aperiam
        qui, quas aspernatur fuga alias, voluptatibus totam adipisci ex cum
        harum error, in omnis est a. Explicabo sint consequatur, id numquam quos
        sequi distinctio quo possimus amet, reprehenderit, eius quisquam dolorem
        nostrum ullam vel aliquam illo tempore molestiae? Tenetur, commodi
        assumenda? Inventore tempore ipsum reiciendis fugiat totam possimus!
        Minus rem minima, reiciendis aspernatur explicabo unde voluptates modi
        provident impedit harum doloremque quis. In eius, culpa aperiam soluta
        repudiandae commodi officia doloremque debitis veritatis amet,
        cupiditate maiores possimus exercitationem impedit odit suscipit cumque
        dignissimos. Voluptatibus eum id deleniti tempore perspiciatis aliquid
        nisi labore consequuntur fugiat repellendus molestiae in odio nobis
        dolorem minus, neque, excepturi ipsa molestias soluta officiis.
        Repudiandae similique dignissimos ipsa provident ipsam, quaerat, soluta
        sunt recusandae laboriosam sit, praesentium perferendis vitae deserunt
        eaque error ab eveniet aliquid. Nihil autem veniam assumenda possimus
        accusamus cupiditate culpa maiores. Atque, optio provident.
      </section>
      <section className="section">Feedback</section>
    </main>
  );
}

export default HomeApp;
