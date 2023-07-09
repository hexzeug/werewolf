import { useTranslation } from 'react-i18next';

function RoomApp() {
  const { t } = useTranslation();
  return (
    <div className="RoomApp">
      <h2>{t('room.title')}</h2>
      <section>
        <p>{t('room.cta')}</p>
      </section>
      <section>
        <form>
          <label>
            {t('room.form.name.label')}
            <input
              type="text"
              name="name"
              id="name"
              placeholder={t('room.form.name.placeholder')}
            />
          </label>
        </form>
      </section>
    </div>
  );
}

export default RoomApp;
