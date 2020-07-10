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

export default {
    parseLinks
}
