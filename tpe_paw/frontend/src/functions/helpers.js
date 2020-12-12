const parseLinks = (linksHeader) => {
  const parsedLinks = linksHeader.split(',').reduce((acc, link) => {
    const match = link.match(/<(.*)>; rel="(\w*)"/)
    const url = match[1]
    const rel = match[2]
    acc[rel] = url
    return acc;
  }, {})
  return parsedLinks;
}

function getTitle (vm) {
  const { title } = vm.$options
  if (title) {
    return typeof title === 'function'
      ? title.call(vm)
      : title
  }
}

export default {
  parseLinks,
  created () {
    const title = getTitle(this)
    if (title) {
      document.title = title
    }
  },
}

export {
  parseLinks
}
