import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import api, { base_url } from '../api';
import RoomLink from './RoomLink';

function HomeApp() {
  const { t } = useTranslation();
  const [status, setStatus] = useState('idle');
  const [players, setPlayers] = useState('');
  const [help, setHelp] = useState(null);
  const [tokens, setTokens] = useState(null);

  const handleChange = (event) => {
    const value = event.target.value;
    const number = Number(value);
    if (isNaN(number) || !value || number < 2 || number > 18) {
      setStatus('error');
      setHelp('home.form.help.error');
    } else {
      setStatus('valid');
      setHelp('');
    }
    setPlayers(value);
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    setStatus('loading');
    const { ok, status, body } = await api.post(`/game?players=${players}`);
    if (!ok) {
      console.error(`game creation failed: ${status}`);
      return;
    }
    setStatus('tokens');
    setTokens(body);
  };
  const base_link = window.location.origin + base_url + 'room?wat=';

  return (
    <main>
      <section className="section">
        {status !== 'tokens' && (
          <div className="hero is-medium">
            <div className="hero-body">
              <form onSubmit={handleSubmit}>
                <div className="field has-addons has-addons-centered">
                  <div className="control is-large">
                    <input
                      className={`input is-large ${
                        status === 'error' && 'is-danger'
                      } ${
                        (status === 'valid' || status === 'loading') &&
                        'is-success'
                      }`}
                      type="text"
                      placeholder={t('home.form.players.placeholder')}
                      value={players}
                      onChange={handleChange}
                      readOnly={status === 'loading'}
                    />
                    {help && <p className="help is-danger">{t(help)}</p>}
                  </div>
                  <div className="control is-large">
                    <button
                      className={`button is-large ${
                        status === 'loading' && 'is-loading'
                      }`}
                      type="submit"
                      disabled={status !== 'valid' && status !== 'loading'}
                    >
                      {t('home.form.send.value')}
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        )}
        {status === 'tokens' && (
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
                    <a
                      className="button is-fullwidth"
                      href={base_link + tokens[0]}
                    >
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
