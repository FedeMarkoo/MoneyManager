<!DOCTYPE html>
<%
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", -1);
%>
<html>

<HEAD>
    <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
    <META HTTP-EQUIV="Expires" CONTENT="-1">
    <title>Control de guita</title>
    <script src="https://kit.fontawesome.com/a7e4de9cfc.js" crossorigin="anonymous"></script>
    <link href="css/Style.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.5/angular.min.js"></script>
    <script src="http://cdn.zingchart.com/zingchart.min.js"></script>
    <script src="http://cdn.zingchart.com/angular/zingchart-angularjs.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-filter/0.5.16/angular-filter.js"></script>
    <style>
        fas {
            text-align: right;
        }

        .info {
            padding: 1rem 0 0;
            min-height: 680px;
            background: #fff;
            box-sizing: border-box;
        }

        .control-bar {
            margin: 0 auto;
            padding: 0 0 1rem;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .control-bar.loaded {
            display: flex !important;
            opacity: 1;
        }

        .control-bar > div {
            display: flex;
            align-items: center;
        }

        .control-bar > * + * {
            margin-left: 10px;
        }

        .control-bar span {
            margin-left: 7px;
            display: inline-block;
        }

        .control-bar select {
            min-width: 45px;
            height: 40px;
            background: #fff;
            border: 1px solid #ebebeb;
            border-radius: 4px;
        }

        .control-bar .sel-wide {
            min-width: 60px;
        }

        .control-bar button {
            min-width: 45px;
            height: 40px;
            cursor: pointer;
            color: #fff;
            background: #074361;
            border: 1px solid #074361;
            border-radius: 4px;
        }

        .zc-body {
            background-color: #fff;
        }

    </style>
</HEAD>

<body>
<div>
    <div ng-app="myApp" ng-controller="movsCtrl">

        <button class="collapsible">Cargar movimientos</button>
        <div class="content">
            <div>
                <label for="periodo">periodo:</label> <input ng-model="load.periodo" placeholder="Text"
                                                             type="text"/>
            </div>
            <div>
                <label for="visacompra">visa compra:</label> <input ng-model="load.visacompra" placeholder="Text"
                                                                    type="text"/>
            </div>
            <div>
                <label for="visacuota">visa cuota:</label> <input ng-model="load.visacuota" placeholder="Text"
                                                                  type="text"/>
            </div>
            <div>
                <label for="mastercompra">master compra:</label> <input ng-model="load.mastercompra"
                                                                        placeholder="Text" type="text"/>
            </div>
            <div>
                <label for="mastercuota">master cuota:</label> <input ng-model="load.mastercuota" placeholder="Text"
                                                                      type="text"/>
            </div>
            <div>
                <input type="submit" value="Submit" id="button-1" ng-click="sendAJAX()"/>
            </div>
        </div>

        <button class="collapsible">Movimientos de Crytos</button>
        <div class="content">
            <table>
                <tr>
                    <th>Crypto</th>
                    <th>Cantidad</th>
                    <th>Valor en ARS</th>
                    <th>Valor en BTC</th>
                    <th>Valor en USD</th>
                    <th>Ganancia en BTC</th>
                    <th>Ganancia en ARS</th>
                    <th>Valor de moneda en BTC</th>
                    <th>Valor de moneda en ARS</th>
                    <th>Valor de moneda en USD</th>
                </tr>
                <tr ng-repeat="c in crypt">
                    <td>{{c.name | uppercase}}</td>
                    <td>{{c.amount | number:8}}</td>
                    <td>{{c.amountARS | currency}}</td>
                    <td>{{c.amountBTC | number:8}}</td>
                    <td>{{c.amountUSD | currency}}</td>
                    <td>{{c.gananciaBTC | number:8}}</td>
                    <td>{{c.gananciaARS | currency}}</td>
                    <td>{{c.valueBTC | number:8}}</td>
                    <td>{{c.valueARS | currency}}</td>
                    <td>{{c.valueUSD | currency}}</td>
                </tr>
            </table>

            <div zingchart id="chart-1" class="zc-ref" zc-values="cryptoHist"
                 zc-height="700" zc-width="100%" zc-type="area"></div>
            <div zingchart id="chart-2" class="zc-ref" zc-values="cryptoHistB"
                 zc-height="700" zc-width="100%" zc-type="area"></div>
        </div>

        <button class="collapsible">Movimientos de Compras</button>
        <div class="content">
            <p>
                <input type="text" ng-model="fcom" placeholder="Filtro">
                <label>
                    <select ng-model="listForOrder" ng-init="listForOrder='monto'">
                        <option ng-repeat="q in orders" value="{{q.val}}" selected="{{q.default}}">{{q.des}}
                        </option>
                    </select>
                </label>
                <input type="checkbox" ng-model="descCompra" ng-init="descCompra=true">
                <input type="text" ng-model="periodoS" placeholder="Periodo" ng-change="getPeriodo()">
            </p>
            <table style="float: left;">
                <tr>
                    <th>Fecha</th>
                    <th>Descripcion</th>
                    <th>Tipo</th>
                    <th>Comprobante</th>
                    <th>Origen</th>
                    <th>Monto {{ getTotalCompra() | currency}}</th>
                    <th>Dolar</th>
                    <th>Clasificacion</th>
                </tr>
                <tr ng-repeat="x in compras=(movs | filter : {$ : fcom}) | orderBy: listForOrder: descCompra">
                    <td>{{x.fecha | date:'dd/MM/yyyy'}}</td>
                    <td>{{x.descripcion}}</td>
                    <td>{{x.tipo}}</td>
                    <td>{{x.comprobante}}</td>
                    <td>{{x.origen}}</td>
                    <td>{{x.monto | currency}}</td>
                    <td>{{x.dolar | currency}}</td>
                    <td>
                        <select ng-model="x.clasificacion" ng-options="b for b in clasificaciones track by b"
                                ng-change="updateClasificaciones(x)"></select>
                    </td>
                </tr>
            </table>
            <table style="float: left;">
                <tr>
                    <th>Clasificaciones</th>
                    <th>Monto</th>
                </tr>
                <tr ng-repeat="(clasificacion, montos) in movs | groupBy: 'clasificacion'">
                    <td>{{clasificacion}}</td>
                    <td>{{sumClasificaciones(montos) | currency}}</td>
                </tr>
                <tr>
                    <th>Tipos</th>
                    <th>Monto</th>
                </tr>
                <tr ng-repeat="(tipo, montos) in compras | groupBy: 'tipo'">
                    <td>{{tipo}}</td>
                    <td>{{sumClasificaciones(montos) | currency}}</td>
                </tr>
            </table>
        </div>

        <button class="collapsible">Movimientos de Cuotas</button>
        <div class="content">
            <p>
                <input type="text" ng-model="fcuo" placeholder="Filtro">
                <select ng-model="listForOrderC" ng-init="listForOrderC='monto'">
                    <option ng-repeat="q in ordersc" value="{{q.val}}">{{q.des}}</option>
                </select>
                <input type="checkbox" ng-model="descCuota" ng-init="descCuota=true">
                <input type="text" ng-model="periodoS" placeholder="Periodo" ng-change="getPeriodo()">
            </p>
            <table>
                <tr>
                    <th>Fecha</th>
                    <th>Descripcion</th>
                    <th>Comprobante</th>
                    <th>Origen</th>
                    <th>Proximo {{getTotalProximo() | currency}}</th>
                    <th>Resto cuotas</th>
                    <th>Total cuotas</th>
                    <th>Monto Resto{{getTotalCuota() | currency}}</th>
                    <th>Dolar</th>
                    <th>Monto Total {{getTotalCuota2() | currency}}</th>
                </tr>
                <tr ng-repeat="x in cuota | filter : {'descripcion' : fcuo} | orderBy: listForOrderC: descCuota"
                    ng-model="cuotas">
                    <td>{{x.fecha | date:'dd/MM/yyyy'}}</td>
                    <td>{{x.descripcion}}</td>
                    <td>{{x.comprobante}}</td>
                    <td>{{x.origen}}</td>
                    <td>{{x.monto/x.resto | currency}}</td>
                    <td>{{x.resto}}</td>
                    <td>{{x.total}}</td>
                    <td>{{x.monto | currency}}</td>
                    <td>{{x.dolar | currency}}</td>
                    <td>{{x.monto/x.resto*x.total | currency}}</td>
                </tr>
            </table>
        </div>

        <button class="collapsible">Proyeccion Cuotas</button>
        <div class="content">
            <input type="text" ng-model="fcuoP" placeholder="Filtro">
            <select ng-model="listForOrderP" ng-init="listForOrderP='resto'">
                <option ng-repeat="q in ordersp" value="{{q.val}}">{{q.des}}</option>
            </select>
            <input type="checkbox" ng-model="descCuotaP" ng-init="descCuotaP=false">
            <input type="text" ng-model="periodoS" placeholder="Periodo" ng-change="getPeriodo()">
            <table>
                <tr>
                    <th>Descripcion</th>
                    <th>Origen</th>
                    <th>Resto cuotas</th>
                    <th>Total cuotas</th>
                    <th>Resto</th>
                    <th>{{getMonth(1)}} {{getMonthAmount(1) | currency}}</th>
                    <th>{{getMonth(2)}} {{getMonthAmount(2) | currency}}</th>
                    <th>{{getMonth(3)}} {{getMonthAmount(3) | currency}}</th>
                    <th>{{getMonth(4)}} {{getMonthAmount(4) | currency}}</th>
                    <th>{{getMonth(5)}} {{getMonthAmount(5) | currency}}</th>
                    <th>{{getMonth(6)}} {{getMonthAmount(6) | currency}}</th>
                    <th>{{getMonth(7)}} {{getMonthAmount(7) | currency}}</th>
                    <th>{{getMonth(8)}} {{getMonthAmount(8) | currency}}</th>
                    <th>{{getMonth(9)}} {{getMonthAmount(9) | currency}}</th>
                </tr>
                <tr ng-repeat="x in cuota | filter : {'descripcion' : fcuoP} | orderBy: listForOrderP: descCuotaP"
                    ng-model="proyec">
                    <td>{{x.descripcion}}</td>
                    <td>{{x.origen}}</td>
                    <td>{{x.resto}}</td>
                    <td>{{x.total}}</td>
                    <td>{{x.monto | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,1) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,2) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,3) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,4) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,5) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,6) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,7) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,8) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,9) | currency}}</td>
                </tr>
            </table>
        </div>

        <button class="collapsible">Proyeccion e Historico</button>
        <div class="content">
            <i class="buttonhist fas fa-backward" ng-click="change(-1)"></i> <i class="buttonhist fas fa-eraser"
                                                                                ng-click="change(-defase)"></i><i
                class="buttonhist fas fa-forward" ng-click="change(1)"></i><i
                class="buttonhist fas fa-fast-forward" ng-click="change(6)"></i>
            <table>
                <tr>
                    <th><i class="fas fa-plus-circle" ng-click="addRow()"></i></th>
                    <th ng-repeat="x in proyeccionHistorico['amounts'] track by $index" ng-model="hist">
                        {{getMonth($index-4)}} {{x | currency}}
                    </th>
                </tr>
                <tr ng-repeat="x in proyeccionHistorico['historicos']" ng-model="hist">
                    <td>{{x.decrypt}}<i class="far fa-trash-alt" ng-click="removeHist(x.decrypt)"
                                        ng-if="x.editableType == 1 ||  x.editableType == 2"></i>
                    </td>
                    <td ng-repeat="monto in x.amount track by $index" ng-model="qwe">{{monto | currency}}
                        <i class="far fa-edit"
                           ng-if="monto != null && (x.editableType == 1 ||  x.editableType == 2) && $index + defase >= 3"
                           ng-click="edit(x,$index,$parent.$index)"></i>
                        <i class="fas fa-plus"
                           ng-if="monto == null && x.editableType == 2 && ($index + defase == 4 || $index + defase == 3)"
                           ng-click="edit(x,$index,$parent.$index); contenteditable='true'"></i>
                    </td>
                </tr>
                <tr ng-repeat="x in proyeccionHistorico['newRow']" ng-model="hist" id="nueva">
                    <td contenteditable="true">{{x.decrypt}}</td>
                    <td contenteditable="true" ng-repeat="monto in x.amount track by $index" ng-model="qwe">
                        {{monto | currency}}
                    <td><i class="fas fa-check-double" ng-click="saveNew()"></i></td>
                </tr>
                </tr>
            </table>
        </div>
    </div>
</div>

<script>
    angular.module('myApp', ['angular.filter', 'zingchart-angularjs']).controller('movsCtrl',
        function ($scope, $http, $timeout) {
            $scope.defase = 0;

            $scope.getCrypto = function () {
                $http.get("bitso").then(function (response) {
                    $scope.crypt = response.data;
                }).then(
                    $http.get("bitso/historial").then(function (response) {
                        $scope.cryptoHist = response.data;
                    }).then(
                        $http.get("bitso/historialVB").then(function (response) {
                            $scope.cryptoHistB = response.data;
                        })));
                $timeout($scope.getCrypto, 10000);
            }

            function doGet() {
                if ($scope.periodoS && $scope.periodoS.match("\\d{2}-\\d{4}"))
                    $http.get("movs/get/" + $scope.periodoS).then(function (response) {
                        $scope.movs = response.data.movimientos;
                        $scope.cuota = response.data.cuotas;
                    });
                else if (!$scope.periodoS)
                    $http.get("movs/get").then(function (response) {
                        $scope.movs = response.data.movimientos;
                        $scope.cuota = response.data.cuotas;
                    });
                $scope.getCrypto();
            }


            $scope.getPeriodo = function () {
                doGet();
            }

            $scope.sumClasificaciones = function (items) {
                return items
                    .map(function (x) {
                        return x.monto;
                    })
                    .reduce(function (a, b) {
                        return a + b;
                    });
            };

            $scope.updateClasificaciones = function (mov) {
                $http.put("movs/updateClasif", mov).then(function () {
                    doGet();
                });
            }

            $scope.removeHist = function (desc) {
                $http.delete("movs/removeHistorico/" + desc).then(function () {
                    $http.get("movs/getProyeccionHistorico/" + $scope.defase).then(function (response) {
                        $scope.proyeccionHistorico = response.data;
                    });
                });
            }

            $scope.change = function (a) {
                $scope.defase += a;

                $http.get("movs/getProyeccionHistorico/" + $scope.defase).then(function (response) {
                    $scope.proyeccionHistorico = response.data;
                });
            }

            $scope.edit = function (item, index, parentIndex) {
                $scope.editedItem = angular.copy(item);
                $scope.editedItem.index = index;
                var temp = document.querySelector("body > div > div > div:nth-child(10) > table > tbody > tr:nth-child(" + (this.$parent.$parent.$index + 2) + ") > td:nth-child(" + (this.$parent.$index + 2) + ")");
                temp.contentEditable = "true";
                temp.innerText = this.monto;
                temp.onkeyup = function (a) {
                    asd = this;

                    $scope.editedItem.amount[$scope.editedItem.index] = asd.innerText.trim();
                }
                temp.onblur = function (a) {
                    $scope.saveModif();
                }
            };

            $scope.addRow = function () {
                $scope.proyeccionHistorico['newRow'] = [{
                    decrypt: "Nuevo",
                    amount: [null, null, null, null, null, null, null, null, null]
                }]
            };

            $scope.saveModif = function () {
                $http.post("movs/modifHistorico", $scope.editedItem).then(function () {
                    $http.get("movs/getProyeccionHistorico/" + $scope.defase).then(function (response) {
                        $scope.proyeccionHistorico = response.data;
                    });
                });
            }

            $scope.saveNew = function () {
                let arr = $('#nueva').find('td').get().map(function (cell) {
                    return cell.innerText;
                });
                let decrypt = arr.splice(0, 1)[0];
                let amounts = arr.splice(0, 9);
                let tipo = 1;
                for (let i = 0; i < amounts.length; i++) {
                    if (amounts[i] == "")
                        amounts[i] = null;
                    else if (amounts[i].split("x")) {
                        if (amounts[i].split("x")[0] < 0) {
                            tipo = 1;
                        }
                        for (let q = amounts[i].split("x")[1] - 1; q >= 0; q--) {
                            amounts[i + q] = amounts[i].split("x")[0];
                        }
                    }
                }

                let hist = {};
                hist['decrypt'] = decrypt;
                hist['amount'] = amounts;
                hist['type'] = tipo;

                $http.post("movs/newHistorico", hist).then(function () {
                    $http.get("movs/getProyeccionHistorico/" + $scope.defase).then(function (response) {
                        $scope.proyeccionHistorico = response.data;
                    });
                });
            }

            $scope.getTotalCompra = function () {
                let total = 0;
                if (!$scope.compras) return;
                $scope.compras.forEach(function (a) {
                    total += a.monto;
                });
                return total;
            }

            $scope.getTotalCuota = function () {
                let total = 0;
                if (!$scope.cuotas) return;
                $scope.cuotas.forEach(function (a) {
                    total += a.monto;
                });
                return total;
            }

            $scope.getTotalCuota2 = function () {
                let total = 0;
                if (!$scope.cuotas) return;
                $scope.cuotas.filter(function (a) {
                    return !$scope.fcuo || a.descripcion.toLowerCase().match($scope.fcuo.toLowerCase())
                }).forEach(function (a) {
                    total += a.monto / a.resto * a.total;
                });
                return total;
            }

            $scope.getTotalProximo = function () {
                let total = 0;
                if (!$scope.cuotas) return;
                $scope.cuotas.forEach(function (a) {
                    if (a.resto && a.monto)
                        total += a.monto / a.resto;
                });
                return total;
            }


            $scope.showAmountMonth = function (monto, resto, columna) {
                if (resto < columna) return null;
                if (!resto) return null;
                return monto / resto;
            }

            $scope.sendAJAX = function () {
                $http.post("movs/setPeriodo", $scope.load.periodo).then(function () {
                    $http.post("movs/visa1", $scope.load.visacompra).then(function () {
                        $http.post("movs/visa2", $scope.load.visacuota).then(function () {
                            $http.post("movs/master1", $scope.load.mastercompra).then(function () {
                                $http.post("movs/master2", $scope.load.mastercuota).then(function () {
                                    doGet();
                                })
                            })
                        })
                    })
                });
            }

            meses = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];

            $scope.getMonth = function (plus) {
                d = new Date();
                d.setMonth(d.getMonth() + $scope.defase + plus);
                return meses[(d.getMonth()) % 12] + " " + d.getFullYear();
            }

            $scope.customSort = function (param) {
                if (param.resto)
                    return param.monto / param.resto;
                return -9999;
            }

            $scope.getMonthAmount = function (plus) {
                let total = 0;
                if (!$scope.cuotas) return;
                $scope.cuota.filter(function (a) {
                    return a.resto >= plus;
                }).forEach(function (a) {
                    total += a.monto / a.resto;
                });
                return total;
            }

            doGet();

            $http.get("movs/getProyeccionHistorico/" + $scope.defase).then(function (response) {
                $scope.proyeccionHistorico = response.data;
            });

            $scope.clasificaciones = [
                "Comida",
                "Ferreteria",
                "Construccion",
                "Estudio",
                "Ahorros",
                "Bazar",
                "Auto",
                "Internet",
                "Farmacia"
            ];

            $scope.orders = [
                {des: "Tipo", val: "tipo", default: " ng-selected=\"{{q.default}}\""}
                , {des: "Monto", val: "monto", default: true}
                , {des: "Dolar", val: "dolar", default: false}
                , {des: "Fecha", val: "fecha", default: false}
                , {des: "Descripcion", val: "descripcion", default: false}
            ];

            $scope.ordersc = [
                {des: "Monto", val: "monto", default: "selected"}
                , {des: "Dolar", val: "dolar", default: false}
                , {des: "Fecha", val: "fecha", default: false}
                , {des: "Total", val: "total", default: false}
                , {des: "Resto", val: "resto", default: false}
                , {des: "Descripcion", val: "descripcion", default: false}
            ];

            $scope.ordersp = [
                {des: "Monto", val: "customSort", default: "selected"}
                , {des: "Dolar", val: "dolar", default: false}
                , {des: "Fecha", val: "fecha", default: false}
                , {des: "Total", val: "total", default: false}
                , {des: "Resto", val: "resto", default: false}
                , {des: "Descripcion", val: "descripcion", default: false}
            ];

        })
    ;


</script>
<script>
    var coll = document.getElementsByClassName("collapsible");
    var i;

    for (i = 0; i < coll.length; i++) {
        coll[i].addEventListener("click", function () {
            const a = document.getElementsByClassName("content");
            const content = this.nextElementSibling;
            const max = content.style.maxHeight;
            for (let i = 0; i < a.length; i++) {
                a[i].style.maxHeight = null;
                a[i].previousElementSibling.classList.remove("active");
            }
            if (max) {
                content.style.maxHeight = null;
            } else {
                this.classList.toggle("active");
                content.style.maxHeight = content.scrollHeight * 10 + "px";
            }
        });
    }
</script>
</body>

</html>