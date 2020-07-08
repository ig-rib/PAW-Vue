const localDomain = 'http://localhost:8080/webapp_war_exploded/'

const urls = {
  user: {
    active: localDomain + 'user/:id/active'
  },
  test: {
    jerseyTest: localDomain + 'jersey-test'
  }
}

export default urls
