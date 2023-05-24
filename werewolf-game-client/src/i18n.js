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
        'narrator.werewolves.awake': 'Die Werwölfe erwachen.',
        'roles.cupid': 'Armor',
        'roles.hunter': 'Jäger',
        'roles.seer': 'Seherin',
        'roles.villager': 'Dorfbewohner',
        'roles.werewolf': 'Werwolf',
        'roles.witch': 'Hexe',
        'player.alt.in_love': 'verliebt',
        'player.snore_symbol_3_char': 'zzz',
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
