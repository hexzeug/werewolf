import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

i18n.on('languageChanged', (lng) => {
  document.title = i18n.t('document.title');
  document.documentElement.setAttribute('lang', lng);
  document.documentElement.setAttribute('dir', i18n.dir());
});

i18n.use(initReactI18next).init({
  debug: true,
  lng: 'de',
  interpolation: {
    escapeValues: false,
  },
  resources: {
    de: {
      translation: {
        'document.title': 'Werwolf Game Client',
        'narrator.generic.sunset': 'Das Dorf schläft ein.',
        'narrator.werewolves.awake': 'Die Werwölfe erwachen.',
        'roles.cupid': 'Armor',
        'roles.hunter': 'Jäger',
        'roles.seer': 'Seherin',
        'roles.villager': 'Dorfbewohner',
        'roles.werewolf': 'Werwolf',
        'roles.witch': 'Hexe',
        'player.alt.inLove': 'verliebt',
        'player.snoreSymbol3Char': 'zzz',
        'chat.input.placeholder': 'Hier schreiben...',
        'chat.input.send': 'Senden',
        'witch.healing.question': 'Willst du das Opfer heilen?',
        'witch.healing.answer.yes': 'Ja',
        'witch.healing.answer.no': 'Nein',
      },
    },
  },
});

export default i18n;
