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
        'home.form.players.placeholder': 'Spieleranzahl',
        'home.form.send.value': 'Spiel erstellen',
        'home.form.help.error': 'Gib eine Zahl zwischen 2 und 18 ein',
        'home.form.tooltip.cannot_create': 'Erstellen fehlgeschlagen',
        'home.link.tooltip.copied': 'Kopiert',
        'home.link.tooltip.cannot_copy': 'Konnte nicht kopiert werden',
        'home.game.title': 'Dein Spiel wurde erstellt.',
        'home.game.info.others':
          'Jeder Mitspieler joint mit einem Link. Teile jedem deiner Mitspieler einen dieser Links.',
        'home.game.info.disclaimer':
          'In Zukunft wird das selbstverständlich verbessert und man muss nicht mehr jedem einen anderen Link schicken.',
        'home.game.info.self': 'Gib nun hier deinen eigenen Namen ein.',
        'home.game.link': 'Klicke hier, nachdem du alle Links geteilt hast',
        'home.game.you': 'Du',
        'home.game.player': 'Spieler',
        'home.game.copy': 'Kopieren',
        'room.title': 'Neuer Raum',
        'room.form.name.placeholder': 'Spielername',
        'room.form.send.value': 'Absenden',
        'room.join.help.error':
          'Dein Name darf nicht kürzer als 2 oder länger als 20 Zeichen sein.',
        'room.join.waiting.title': 'Willkommen {{name}}',
        'room.join.waiting.info':
          'Bitte warte bis die anderen Spieler ihren Namen festgelegt haben.',
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
