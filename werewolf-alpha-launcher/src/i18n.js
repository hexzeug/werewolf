import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

await i18n.use(initReactI18next).init({
  debug: true,
  lng: 'de',
  interpolation: {
    escapeValues: false,
  },
  resources: {
    de: {
      translation: {
        'document.title': 'Werwolf Alpha Launcher',
        'room.title': 'Neuer Raum',
        'room.cta': 'Gib deinen Namen ein!',
        'room.form.name.label': 'Name',
        'room.form.name.placeholder': 'Spielername',
      },
    },
  },
});

i18n.on('languageChanged', (lng) => {
  document.title = i18n.t('document.title');
  document.documentElement.setAttribute('lang', lng);
  document.documentElement.setAttribute('dir', i18n.dir());
});

export default i18n;
