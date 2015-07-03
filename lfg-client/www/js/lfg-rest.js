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
                    setToken: function (newtoken) {
                        console.log("$lfgRest.setToken");
                        token = newtoken;
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
                        console.log("$lfgRest.getUser");
                        console.log(token.value);
                        var config = {
                            url: BASE_URL + '/user/me',
                            method: "GET",
                            headers: {
                                "x-token": token.value
                            }
                        };
                        var req = $http(config);

                        console.log(req);
                        return req;
                    },
                    createToken: function (login, password) {
                        console.log("$lfgRest.createToken");
                        return $http.post(BASE_URL + '/token/create',
                            {
                                login: login,
                                password: password,
                                nickname: "ionic"
                            });
                    },
                    refreshToken: function () {
                        token = $http.post(
                            BASE_URL + '/token/refresh',
                            {
                                accessToken: token.accessToken,
                                refreshToken: token.refreshToken
                            }
                        );
                        return token;
                    },
                    destroyToken: function () {
                        token = undefined;
                    }
                };
            }
        };
    });
