const { render } = require('@testing-library/react');
const { default: Spinner } = require('./Spinner');

describe('spinner component', () => {
  it('should render without throwing', () => {
    render(<Spinner />);
  });
});
