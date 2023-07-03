import api from './api';
import { addEventListener } from './eventReceiver';

export const loadGame = async () => {
  addEventListener('phaseevent', handlePhaseEvent);
  const me = await api.get('/me');
  console.log(me);
};

const handlePhaseEvent = (phase) => {};
