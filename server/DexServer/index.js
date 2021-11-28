const http=require('http')
const fs=require('fs')
const server=http.createServer((req,res)=>{

    const stat=fs.statSync('./DexClass.dex')

    res.writeHead(200,{
        'Content-Type': 'application/dex',
        'Content-Length': stat.size
    })

    const file=fs.createReadStream('./DexClass.dex')
    file.pipe(res)
})
server.listen(3000)

