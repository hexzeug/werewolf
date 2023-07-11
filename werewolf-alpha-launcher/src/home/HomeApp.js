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
            <h1>About</h1>
            <p>
              Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure
              aperiam qui, quas aspernatur fuga alias, voluptatibus totam
              adipisci ex cum harum error, in omnis est a. Explicabo sint
              consequatur, id numquam quos sequi distinctio quo possimus amet,
              reprehenderit, eius quisquam dolorem nostrum ullam vel aliquam
              illo tempore molestiae? Tenetur, commodi assumenda? Inventore
              tempore ipsum reiciendis fugiat totam possimus! Minus rem minima,
              reiciendis aspernatur explicabo unde voluptates modi provident
              impedit harum doloremque quis. In eius, culpa aperiam soluta
              repudiandae commodi officia doloremque debitis veritatis amet,
              cupiditate maiores possimus exercitationem impedit odit suscipit
              cumque dignissimos. Voluptatibus eum id deleniti tempore
              perspiciatis aliquid nisi labore consequuntur fugiat repellendus
              molestiae in odio nobis dolorem minus, neque, excepturi ipsa
              molestias soluta officiis. Repudiandae similique dignissimos ipsa
              provident ipsam, quaerat, soluta sunt recusandae laboriosam sit,
              praesentium perferendis vitae deserunt eaque error ab eveniet
              aliquid. Nihil autem veniam assumenda possimus accusamus
              cupiditate culpa maiores. Atque, optio provident.
            </p>
          </div>
        </div>
      </section>
      <section className="section">
        <div className="container">
          <div className="content">
            <h1>Feedback</h1>
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
