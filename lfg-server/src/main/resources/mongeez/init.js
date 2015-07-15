//mongeez formatted javascript
//changeset gop:init runAlways:true
db.users.insert({
    login : "admin",
    email : "admin@lfg.com",
    encodedPassword : "53ee8cdeb80b8e8fe9fe3e444d16cd8be73978037f777ba5abf3e0390ab4a4ef",
    salt:"pÓ|äj€)‰xã€�-‘½„©”m\rò*3¨…mÉ‹\u001eŽÔú",
    creationTs:0,
    updateTs:0,
    roles:["ADMIN","USER"],
    tokens:{},
    profiles:[]
});

//changeset gop:resetadmin runAlways:true
db.users.update({login:"admin"},{$set:{
    email : "admin@lfg.com",
    encodedPassword : "53ee8cdeb80b8e8fe9fe3e444d16cd8be73978037f777ba5abf3e0390ab4a4ef",
    salt:"pÓ|äj€)‰xã€�-‘½„©”m\rò*3¨…mÉ‹\u001eŽÔú",
    creationTs:0,
    updateTs:0,
    roles:["ADMIN","USER"],
    tokens:{},
    profiles:[]
}});