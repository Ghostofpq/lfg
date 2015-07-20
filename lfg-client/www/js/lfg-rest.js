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
            $get: function ($http, $window) {
                return {
                    //UTILS
                    isLoggedIn: function () {
                        console.log("$lfgRest.isLoggedIn");
                        return !angular.equals({}, this.getUserLocal());
                    },
                    logOut: function () {
                        console.log("$lfgRest.logOut");
                        $window.localStorage["x-user"] = {};
                        $window.localStorage["x-token"] = {};
                    },
                    // TOKENS
                    hasToken: function () {
                        console.log("$lfgRest.hasToken");
                        console.log(this.getToken());
                        return ((typeof this.getToken() != "undefined") && (!angular.equals({}, this.getToken())));
                    },
                    getToken: function () {
                        console.log("$lfgRest.getToken");
                        try {
                            return JSON.parse($window.localStorage["x-token"] || "{}");
                        } catch (e) {
                            this.logOut();
                            return {};
                        }
                    },
                    setToken: function (token) {
                        console.log("$lfgRest.setToken");
                        $window.localStorage["x-token"] = token.value;
                    },
                    createToken: function (login, password) {
                        console.log("$lfgRest.createToken");
                        return $http.post(BASE_URL + '/token/create', {
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
                    },
                    // USERS    
                    getUser: function () {
                        console.log("$lfgRest.getUser");
                        return $http({
                            method: 'GET',
                            url: BASE_URL + '/user/me',
                            headers: {
                                'x-token': $window.localStorage["x-token"]
                            }
                        });
                    },
                    getUserLocal: function () {
                        console.log("$lfgRest.getUserLocal");
                        try {
                            return JSON.parse($window.localStorage["x-user"] || "{}")
                        } catch (e) {
                            this.logOut();
                            return {};
                        }
                    },
                    setUserLocal: function (user) {
                        console.log("$lfgRest.setUserLocal");
                        $window.localStorage["x-user"] = JSON.stringify(user);
                    },
                    registerUser: function (login, password, email) {
                        console.log("$lfgRest.registerUser");
                        return $http.post(
                            BASE_URL + '/user', {
                                email: email,
                                password: password,
                                login: login
                            }
                        );
                    },
                    // UTILS
                    isLoginFree: function (login) {
                        console.log("$lfgRest.isLoginFree");
                        return $http.get(BASE_URL + '/utils/isLoginFree/'+login);
                    }

                };
            }
        };
    });
