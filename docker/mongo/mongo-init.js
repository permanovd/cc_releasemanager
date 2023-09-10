db.createUser({
    user: 'root',
    pwd: 'rootpassword',
    roles: [
        {
            role: 'dbOwner',
            db: 'AppVersions',
        },
    ],
});