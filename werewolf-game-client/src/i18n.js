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
        'narrator.generic.sunrise': 'Das Dorf erwacht.',
        'narrator.generic.sunset': 'Das Dorf schläft ein.',
        'narrator.cupid.awake': 'Der Armor erwacht.',
        'narrator.cupid.action':
          'Er sucht sich zwei Spieler aus, die sich verlieben werden.',
        'narrator.cupid.asleep': 'Und schläft wieder ein.',
        'narrator.couple.awake': 'Das verliebte Paar erwacht.',
        'narrator.couple.action': 'Sie gucken sich in die Augen.',
        'narrator.couple.asleep': 'Und schlafen wieder ein.',
        'narrator.seer.awake': 'Die Seherin erwacht.',
        'narrator.seer.action':
          'Sie sucht eine Person aus, deren wahre Identität sie sehen möchte.',
        'narrator.seer.done': 'Sie hat jemanden ausgesucht.',
        'narrator.seer.asleep': 'Sie schläft wieder ein.',
        'narrator.werewolves.awake': 'Die Werwölfe erwachen.',
        'narrator.werewolves.action': 'Sie suchen sich ein Opfer aus.',
        'narrator.werewolves.done': 'Sie haben ihr Opfer gefunden.',
        'narrator.werewolves.asleep': 'Die Werwölfe schlafen wieder ein.',
        'narrator.witch.awake': 'Die Hexe erwacht.',
        'narrator.witch.info': 'Sie sieht das Opfer der Werwölfe.',
        'narrator.witch.action.heal': 'Und überlegt, ob sie es heilen soll.',
        'narrator.witch.done.heal': 'Sie hat sich entschieden.',
        'narrator.witch.action.poison':
          'Sie überlegt nun, ob sie jemanden vergiften möchte.',
        'narrator.witch.done.poison': 'Sie hat sich entschieden.',
        'narrator.witch.asleep': 'Die Hexe schläft wieder ein.',
        'narrator.hunter.action':
          'Da {{player.name}} Jäger war, erschießt er jemanden.',
        'narrator.hunter.done': 'Er hat geschossen.',
        'narrator.death.night':
          '{{player.name}} ist gestorben. Er war ein(e) $t(roles.{{player.role}}).',
        'narrator.death.executed':
          '{{player.name}} wurde hingerichtet. Er war ein(e) $t(roles.{{player.role}}).',
        'narrator.death.shot':
          '{{player.name}} wurde erschossen. Er war ein(e) $t(roles.{{player.role}}).',
        'narrator.death.heartbreak':
          '{{player.name}} ist an Liebeskummer gestorben. Er war ein(e) $t(roles.{{player.role}}).',
        'narrator.death.own.night': 'Du bist gestorben.',
        'narrator.death.own.executed': 'Du wurdest hingerichtet.',
        'narrator.death.own.shot': 'Du wurderst erschossen.',
        'narrator.death.own.heartbreak': 'Du bist an Liebeskummer gestorben.',
        'narrator.winner': 'Die {{winner}} haben gewonnen.',
        'witch.question.heal': 'Willst du das Opfer heilen?',
        'witch.question.poison':
          'Willst du noch jemand anderen töten? Wenn ja wähle ihn aus.',
        'witch.info.no_heal': 'Du hast deinen Heiltrank schon aufgebraucht.',
        'witch.info.no_poison': 'Du hast deinen Gifttrank schon aufgebraucht.',
        'witch.answer.yes': 'Ja',
        'witch.answer.no': 'Nein',
        'witch.answer.continue': 'Weiter',
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
      },
    },
  },
});

export default i18n;
