angular.module('lfg.rest', [])
    .provider("$lfgRest", function () {
        console.log("define lfgRest");
        var BASE_URL = "http://localhost:8080/api";
        var token = undefined;

        var urlValue = function (v) {
            if (angular.isUndefined(v) || v === null || v === "") {
                return "null";
            } else {
                return v;
            }
        };

        return {
            $get: function ($http) {
                return {
                    hasToken: function () {
                        console.log("$lfgRest.hasToken");
                        return typeof token != "undefined";
                    },
                    registerUser: function (login, password, email) {
                        return $http.post(
                            BASE_URL + '/user',
                            {
                                email: email,
                                password: password,
                                login: login
                            }
                        );
                    },
                    getUser: function () {
                        console.log(TOKEN.accessToken);
                        var req = {
                            method: 'GET',
                            url: BASE_URL + '/user/me',
                            headers: {
                                'ACCESS-TOKEN': TOKEN.accessToken
                            }
                        };
                        $http(req)
                            .success(function (res) {
                                return res;
                            })
                            .error(function (err) {
                                console.log(err);
                                return err;
                            });
                    },
                    createToken: function (login, password) {
                        console.log("createToken");
                        $http.post(BASE_URL + '/token/create',
                            {
                                login: login,
                                password: password,
                                nickname: "ionic"
                            }
                        )
                            .success(function (res) {
                                TOKEN = res;
                                console.log(TOKEN);
                                return TOKEN;
                            })
                            .error(function (err) {
                                console.log(err);
                                return err;
                            });

                    },
                    refreshToken: function () {
                        TOKEN = $http.post(
                            BASE_URL + '/token/refresh',
                            {
                                accessToken: TOKEN.accessToken,
                                refreshToken: TOKEN.refreshToken
                            }
                        );
                        return TOKEN;
                    },
                    destroyToken: function () {
                        TOKEN = undefined;
                    }
                };
            }
        };
    });
