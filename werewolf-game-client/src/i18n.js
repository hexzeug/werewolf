import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

i18n.use(initReactI18next).init({
  debug: true,
  lng: 'de',
  interpolation: {
    escapeValues: false,
  },
  resources: {
    de: {
      translation: {
        'roles.werewolf': 'Werwolf',
        'roles.villager': 'Dorfbewohner',
        'roles.witch': 'Hexe',
        'roles.seer': 'Seherin',
        'roles.cupid': 'Armor',
        'roles.hunter': 'Jäger',
      },
    },
  },
});
