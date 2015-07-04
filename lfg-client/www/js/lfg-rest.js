angular.module('lfg.rest', [])
    .provider("$lfgRest", function () {
        console.log("define lfgRest");
        var BASE_URL = "http://localhost:8080/api";
        var urlValue = function (v) {
            if (angular.isUndefined(v) || v === null || v === "") {
                return "null";
            } else {
                return v;
            }
        };

        return {
            $get: function ($http,$window) {
                return {
                    hasToken: function () {
                        console.log("$lfgRest.hasToken");
                        return typeof $window.localStorage["x-token"] != "undefined";
                    },
                    setToken: function (newtoken) {
                        console.log("$lfgRest.setToken");
                        $window.localStorage["x-token"]=newtoken.value;
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
                        return $http({
                            method: 'GET',
                            url: BASE_URL + '/user/me',
                            headers: {
                                'x-token':$window.localStorage["x-token"]
                            }
                        });
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
                        // TODO
                    },
                    destroyToken: function () {
                        token = undefined;
                    }
                };
            }
        };
    });
