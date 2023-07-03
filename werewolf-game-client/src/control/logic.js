import { addEventListener } from './eventReceiver';

export const loadGame = async () => {
  addEventListener('phaseevent', handlePhaseEvent);
};

const handlePhaseEvent = (phase) => {};
