/** A Kotlin value the export cannot express, such as an `Instant`: opaque to TypeScript. */
export declare interface KtOpaque<N extends string> {
    readonly __kotlinOpaque: N;
    readonly __doNotUseOrImplementIt: {
        readonly "kotlin.Opaque": unique symbol;
    };
}
/**
 * The JSON a Kotlin value of type `T` is sent as: a list or a set is an array, a map an object
 * (whose keys are optional when they are an enum's names), a `Long` a number, an enum its name,
 * an opaque value a string, and a nullable property may be absent. The exported types describe
 * the Kotlin objects; a body read from or written to the wire is a `Json<T>`, and the compiled
 * code accepts none: an instance of an exported class that implements an exported interface, or
 * of a data class, is refused at any depth.
 *
 * What it does not say: a sealed or polymorphic type carries no discriminant here, so a
 * serializer's `type` field is not typed. A nullable property may be absent: F2's Kotlin client
 * omits nulls (`explicitNulls = false`), Jackson and a default kotlinx `Json` write `null`. A
 * reader that requires the property refuses the JSON that omits it: a default kotlinx `Json`
 * does, Jackson and F2's `Json` read it as `null`. An instance of a class that is neither of the
 * two above is not told apart from a plain object, and a `Long` beyond 2^53 loses its precision
 * as a JavaScript number.
 *
 * These types require `strictNullChecks`: without it, `null` extends every property's type, so
 * every property of a `Json<T>` is optional.
 */
export type Json<T> =
    T extends { asJsReadonlyArrayView(): ReadonlyArray<infer E> } ? Json<E>[]
    : T extends { asJsReadonlySetView(): ReadonlySet<infer E> } ? Json<E>[]
    : T extends { asJsReadonlyMapView(): ReadonlyMap<infer K, infer V> } ? JsonMap<JsonKey<K>, Json<V>>
    : T extends { readonly __kotlinOpaque: string } ? string
    : T extends bigint ? number
    : T extends string | number | boolean | null | undefined ? T
    : T extends { readonly name: infer N extends string; readonly ordinal: infer O extends number }
        ? string extends N ? JsonObject<T> : number extends O ? JsonObject<T> : N
    : T extends ReadonlyArray<infer E> ? Json<E>[]
    : T extends (...args: never[]) => unknown ? never
    : T extends object ? JsonObject<T>
    : T;
type JsonKey<K> = Json<K> extends string | number ? Json<K> : string;
// A map holds the entries it has: keys out of a finite set, such as an enum's names, are optional.
type JsonMap<K extends string | number, V> =
    string extends K ? Record<string, V> : number extends K ? Record<number, V> : { [P in K]?: V };
type JsonField<T, P extends keyof T> =
    P extends "__doNotUseOrImplementIt" ? never : T[P] extends (...args: never[]) => unknown ? never : P;
type JsonFields<T> = { [P in keyof T]-?: JsonField<T, P> }[keyof T];
type JsonOptional<T> = {
    [P in JsonFields<T>]-?: null extends T[P] ? P : undefined extends T[P] ? P : never
}[JsonFields<T>];
// What a Kotlin object has and JSON never does: the brand of an exported interface, the members of a data class.
type JsonObject<T> =
    { [P in Exclude<JsonFields<T>, JsonOptional<T>>]: Json<T[P]> } & { [P in JsonOptional<T>]?: Json<T[P]> }
    & { readonly [P in "__doNotUseOrImplementIt" | Exclude<"equals" | "hashCode", JsonFields<T>>]?: never };
type Nullable<T> = T | null | undefined
declare function KtSingleton<T>(): T & (abstract new() => object);
export declare interface KtList<out E> /* extends Collection<E> */ {
    asJsReadonlyArrayView(): ReadonlyArray<E>;
    readonly __doNotUseOrImplementIt: {
        readonly "kotlin.collections.KtList": unique symbol;
    };
}
export declare namespace KtList {
    function fromJsArray<E>(array: ReadonlyArray<E>): KtList<E>;
}
export declare interface KtMap<K, out V> {
    asJsReadonlyMapView(): ReadonlyMap<K, V>;
    readonly __doNotUseOrImplementIt: {
        readonly "kotlin.collections.KtMap": unique symbol;
    };
}
export declare namespace KtMap {
    function fromJsMap<K, V>(map: ReadonlyMap<K, V>): KtMap<K, V>;
}
export declare interface FileAskQuestionQueryDTO {
    readonly question: string;
    readonly history: KtList<ChatMessageDTO>;
    readonly metadata: ChatMetadataDTO;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.s2.file.domain.features.query.FileAskQuestionQueryDTO": unique symbol;
    };
}
export declare interface FileAskQuestionResultDTO {
    readonly item: string;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.s2.file.domain.features.query.FileAskQuestionResultDTO": unique symbol;
    };
}
export declare interface DirectoryPathDTO {
    readonly objectType: string;
    readonly objectId: string;
    readonly directory: string;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.s2.file.domain.model.DirectoryPathDTO": unique symbol;
    };
}
export declare interface ChatMessageDTO {
    readonly content: string;
    readonly type: string;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.s2.file.domain.model.ChatMessageDTO": unique symbol;
    };
}
export declare interface ChatMetadataDTO {
    readonly targetedFiles: KtList<string>;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.s2.file.domain.model.ChatMetadataDTO": unique symbol;
    };
}
export declare interface FilePathDTO {
    readonly objectType: string;
    readonly objectId: string;
    readonly directory: string;
    readonly name: string;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.s2.file.domain.model.FilePathDTO": unique symbol;
    };
}
export declare interface Command extends Message {
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.Command": unique symbol;
    } & Message["__doNotUseOrImplementIt"];
}
export declare interface Event extends Message {
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.Event": unique symbol;
    } & Message["__doNotUseOrImplementIt"];
}
export declare interface Message {
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.Message": unique symbol;
    };
}
export declare interface Problem extends Message {
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.Problem": unique symbol;
    } & Message["__doNotUseOrImplementIt"];
}
export declare interface Query extends Message {
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.Query": unique symbol;
    } & Message["__doNotUseOrImplementIt"];
}
export declare interface EnvelopeDTO<T> extends WithEnvelopeId, WithEnvelopeData<T>, Message {
    readonly id: string;
    readonly data: T;
    readonly type: string;
    readonly datacontenttype: Nullable<string>;
    readonly specversion: Nullable<string>;
    readonly source: Nullable<string>;
    readonly time: Nullable<string>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.envelope.EnvelopeDTO": unique symbol;
    } & Message["__doNotUseOrImplementIt"] & WithEnvelopeData<unknown>["__doNotUseOrImplementIt"] & WithEnvelopeId["__doNotUseOrImplementIt"];
}
export declare class Envelope<T> implements EnvelopeDTO<T> {
    constructor(id: string, data: T, type: string, datacontenttype?: Nullable<string>, source?: Nullable<string>, time?: Nullable<string>);
    get id(): string;
    get data(): T;
    get type(): string;
    get datacontenttype(): Nullable<string>;
    get source(): Nullable<string>;
    get time(): Nullable<string>;
    get specversion(): string;
    copy(id?: string, data?: T, type?: string, datacontenttype?: Nullable<string>, source?: Nullable<string>, time?: Nullable<string>): Envelope<T>;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: EnvelopeDTO<unknown>["__doNotUseOrImplementIt"];
}
export declare namespace Envelope {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <T>() => Envelope<T>;
    }
}
export declare interface WithEnvelopeData<T> {
    readonly data: T;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.envelope.WithEnvelopeData": unique symbol;
    };
}
export declare interface WithEnvelopeId {
    readonly id: string;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.envelope.WithEnvelopeId": unique symbol;
    };
}
export declare interface F2ErrorDTO extends Problem {
    readonly id: Nullable<string>;
    readonly timestamp: string;
    readonly code: number;
    readonly requestId: Nullable<string>;
    readonly message: string;
    readonly refusal: Nullable<F2RefusalDTO>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.error.F2ErrorDTO": unique symbol;
    } & Problem["__doNotUseOrImplementIt"];
}
export declare interface F2RefusalDTO {
    readonly code: string;
    readonly parameters: KtMap<string, string>;
    readonly citers: KtList<string>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.error.F2RefusalDTO": unique symbol;
    };
}
export declare class F2Refusal implements F2RefusalDTO {
    constructor(code: string, parameters?: KtMap<string, string>, citers?: KtList<string>);
    get code(): string;
    get parameters(): KtMap<string, string>;
    get citers(): KtList<string>;
    copy(code?: string, parameters?: KtMap<string, string>, citers?: KtList<string>): F2Refusal;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: F2RefusalDTO["__doNotUseOrImplementIt"];
}
export declare namespace F2Refusal {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2Refusal;
    }
}
export declare class F2Error implements F2ErrorDTO {
    constructor(message: string, id?: Nullable<string>, timestamp?: string, code?: number, requestId?: Nullable<string>, refusal?: Nullable<F2Refusal>);
    get message(): string;
    get id(): Nullable<string>;
    get timestamp(): string;
    get code(): number;
    get requestId(): Nullable<string>;
    get refusal(): Nullable<F2Refusal>;
    toString(): string;
    readonly __doNotUseOrImplementIt: F2ErrorDTO["__doNotUseOrImplementIt"];
}
export declare namespace F2Error {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2Error;
    }
}
export declare interface Match<T> {
    readonly isNegative: boolean;
    map<R>(transform: (value: T) => R): Match<R>;
    not(): Match<T>;
    and(match: Match<T>): Match<T>;
    or(match: Match<T>): Match<T>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.filter.Match": unique symbol;
    };
}
export declare interface SortDTO {
    readonly property: string;
    readonly ascending: boolean;
    readonly nullsFirst: Nullable<boolean>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.filter.SortDTO": unique symbol;
    };
}
export declare interface PageDTO<out OBJECT> {
    readonly total: number;
    readonly items: KtList<OBJECT>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.page.PageDTO": unique symbol;
    };
}
export declare class Page<out OBJECT> implements PageDTO<OBJECT> {
    constructor(total: number, items: KtList<OBJECT>);
    get total(): number;
    get items(): KtList<OBJECT>;
    copy(total?: number, items?: KtList<OBJECT>): Page<OBJECT>;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: PageDTO<unknown>["__doNotUseOrImplementIt"];
}
export declare namespace Page {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <OBJECT>() => Page<OBJECT>;
    }
}
export declare interface PageQueryDTO extends Query {
    readonly pagination: Nullable<OffsetPaginationDTO>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.page.PageQueryDTO": unique symbol;
    } & Query["__doNotUseOrImplementIt"];
}
export declare interface PageQueryResultDTO<out OBJECT> extends Event, PageDTO<OBJECT> {
    readonly total: number;
    readonly items: KtList<OBJECT>;
    readonly pagination: Nullable<OffsetPaginationDTO>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.page.PageQueryResultDTO": unique symbol;
    } & PageDTO<unknown>["__doNotUseOrImplementIt"] & Event["__doNotUseOrImplementIt"];
}
export declare class PageQuery implements PageQueryDTO {
    constructor(pagination: Nullable<OffsetPaginationDTO>);
    get pagination(): Nullable<OffsetPaginationDTO>;
    copy(pagination?: Nullable<OffsetPaginationDTO>): PageQuery;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: PageQueryDTO["__doNotUseOrImplementIt"];
}
export declare namespace PageQuery {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PageQuery;
    }
}
export declare class PageQueryResult<out OBJECT> implements PageQueryResultDTO<OBJECT> {
    constructor(pagination: Nullable<OffsetPagination>, total: number, items: KtList<OBJECT>);
    get pagination(): Nullable<OffsetPagination>;
    get total(): number;
    get items(): KtList<OBJECT>;
    copy(pagination?: Nullable<OffsetPagination>, total?: number, items?: KtList<OBJECT>): PageQueryResult<OBJECT>;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: PageQueryResultDTO<unknown>["__doNotUseOrImplementIt"];
}
export declare namespace PageQueryResult {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <OBJECT>() => PageQueryResult<OBJECT>;
    }
}
export declare interface Pagination {
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.page.Pagination": unique symbol;
    };
}
export declare interface OffsetPaginationDTO extends Pagination {
    readonly offset: number;
    readonly limit: number;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.page.OffsetPaginationDTO": unique symbol;
    } & Pagination["__doNotUseOrImplementIt"];
}
export declare interface PagePaginationDTO extends Pagination {
    readonly page: Nullable<number>;
    readonly size: Nullable<number>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.dsl.cqrs.page.PagePaginationDTO": unique symbol;
    } & Pagination["__doNotUseOrImplementIt"];
}
export declare class OffsetPagination implements OffsetPaginationDTO {
    constructor(offset: number, limit: number);
    get offset(): number;
    get limit(): number;
    copy(offset?: number, limit?: number): OffsetPagination;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: OffsetPaginationDTO["__doNotUseOrImplementIt"];
}
export declare namespace OffsetPagination {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OffsetPagination;
    }
}
export declare class PagePagination implements PagePaginationDTO {
    constructor(page: Nullable<number>, size: Nullable<number>);
    get page(): Nullable<number>;
    get size(): Nullable<number>;
    copy(page?: Nullable<number>, size?: Nullable<number>): PagePagination;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<unknown>): boolean;
    readonly __doNotUseOrImplementIt: PagePaginationDTO["__doNotUseOrImplementIt"];
}
export declare namespace PagePagination {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PagePagination;
    }
}
/** @deprecated  */
export declare const initHook: {
    get(): unknown;
};
export declare interface HttpF2JsFailure {
    readonly kind: HttpF2JsFailureKind;
    readonly message: string;
    readonly status: Nullable<number>;
    readonly error: Nullable<F2ErrorDTO>;
    readonly body: Nullable<string>;
    readonly __doNotUseOrImplementIt: {
        readonly "f2.client.ktor.http.HttpF2JsFailure": unique symbol;
    };
}
export declare abstract class HttpF2JsFailureKind {
    private constructor();
    static get F2(): HttpF2JsFailureKind & {
        get name(): "F2";
        get ordinal(): 0;
    };
    static get HTTP(): HttpF2JsFailureKind & {
        get name(): "HTTP";
        get ordinal(): 1;
    };
    static get NETWORK(): HttpF2JsFailureKind & {
        get name(): "NETWORK";
        get ordinal(): 2;
    };
    static get UNREADABLE(): HttpF2JsFailureKind & {
        get name(): "UNREADABLE";
        get ordinal(): 3;
    };
    static get CLOSED(): HttpF2JsFailureKind & {
        get name(): "CLOSED";
        get ordinal(): 4;
    };
    static values(): [typeof HttpF2JsFailureKind.F2, typeof HttpF2JsFailureKind.HTTP, typeof HttpF2JsFailureKind.NETWORK, typeof HttpF2JsFailureKind.UNREADABLE, typeof HttpF2JsFailureKind.CLOSED];
    static valueOf(value: string): HttpF2JsFailureKind;
    get name(): "F2" | "HTTP" | "NETWORK" | "UNREADABLE" | "CLOSED";
    get ordinal(): 0 | 1 | 2 | 3 | 4;
}
export declare namespace HttpF2JsFailureKind {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => HttpF2JsFailureKind;
    }
}
export declare class HttpF2JsClient {
    private constructor();
    function<QUERY, RESPONSE>(route: string): F2JsFunction<QUERY, RESPONSE>;
    supplier<RESPONSE>(route: string): F2JsSupplier<RESPONSE>;
    consumer<QUERY>(route: string): F2JsConsumer<QUERY>;
    close(): void;
}
export declare namespace HttpF2JsClient {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => HttpF2JsClient;
    }
}
export declare class F2JsFunction<QUERY, RESPONSE> {
    private constructor();
    invoke(msgs: Array<Json<QUERY>>, signal?: Nullable<AbortSignal>): Promise<Array<Json<RESPONSE>>>;
}
export declare namespace F2JsFunction {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <QUERY, RESPONSE>() => F2JsFunction<QUERY, RESPONSE>;
    }
}
export declare class F2JsSupplier<RESPONSE> {
    private constructor();
    invoke(signal?: Nullable<AbortSignal>): Promise<Array<Json<RESPONSE>>>;
}
export declare namespace F2JsSupplier {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <RESPONSE>() => F2JsSupplier<RESPONSE>;
    }
}
export declare class F2JsConsumer<QUERY> {
    private constructor();
    invoke(msgs: Array<Json<QUERY>>, signal?: Nullable<AbortSignal>): Promise<void>;
}
export declare namespace F2JsConsumer {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <QUERY>() => F2JsConsumer<QUERY>;
    }
}
export declare function httpF2JsClient(urlBase: string, headers?: Nullable<() => Promise<Json<KtMap<string, string>>>>): HttpF2JsClient;
export declare interface Automate {
    getAvailableTransitions(state: S2State): Array<S2Transition>;
    isAvailableTransition(currentState: S2State, msg: Message): boolean;
    isAvailableInitTransition(command: Message): boolean;
    isFinalState(state: S2State): boolean;
    isSameState(from: Nullable<S2State>, to: S2State): boolean;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.Automate": unique symbol;
    };
}
export declare abstract class ErrorCategory {
    private constructor();
    static get Rejected(): ErrorCategory & {
        get name(): "Rejected";
        get ordinal(): 0;
    };
    static get Transient(): ErrorCategory & {
        get name(): "Transient";
        get ordinal(): 1;
    };
    static get Indeterminate(): ErrorCategory & {
        get name(): "Indeterminate";
        get ordinal(): 2;
    };
    static get Conflict(): ErrorCategory & {
        get name(): "Conflict";
        get ordinal(): 3;
    };
    static values(): [typeof ErrorCategory.Rejected, typeof ErrorCategory.Transient, typeof ErrorCategory.Indeterminate, typeof ErrorCategory.Conflict];
    static valueOf(value: string): ErrorCategory;
    get name(): "Rejected" | "Transient" | "Indeterminate" | "Conflict";
    get ordinal(): 0 | 1 | 2 | 3;
}
export declare namespace ErrorCategory {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ErrorCategory;
    }
}
export declare class S2Automate implements Automate {
    constructor(name: string, version: Nullable<string>, transitions: Array<S2Transition>);
    get name(): string;
    get version(): Nullable<string>;
    get transitions(): Array<S2Transition>;
    get withResultAsAction(): boolean;
    getAvailableTransitions(state: S2State): Array<S2Transition>;
    isAvailableTransition(currentState: S2State, msg: Message): boolean;
    isAvailableInitTransition(command: Message): boolean;
    isFinalState(state: S2State): boolean;
    isSameState(from: Nullable<S2State>, to: S2State): boolean;
    readonly __doNotUseOrImplementIt: Automate["__doNotUseOrImplementIt"];
}
export declare namespace S2Automate {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2Automate;
    }
}
export declare interface S2InitCommand extends Command {
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.S2InitCommand": unique symbol;
    } & Command["__doNotUseOrImplementIt"];
}
export declare interface S2Command<ID extends unknown> extends Command, WithId<ID> {
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.S2Command": unique symbol;
    } & WithId<unknown>["__doNotUseOrImplementIt"] & Command["__doNotUseOrImplementIt"];
}
export declare interface S2Error {
    readonly type: string;
    readonly description: string;
    readonly date: string;
    readonly payload: KtMap<string, string>;
    readonly cause: Nullable<Error>;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.S2Error": unique symbol;
    };
}
export declare class S2ErrorBase implements S2Error {
    constructor(type: string, description: string, date: string, payload: KtMap<string, string>, cause?: Nullable<Error>);
    get type(): string;
    get description(): string;
    get date(): string;
    get payload(): KtMap<string, string>;
    get cause(): Nullable<Error>;
    toString(): string;
    readonly __doNotUseOrImplementIt: S2Error["__doNotUseOrImplementIt"];
}
export declare namespace S2ErrorBase {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2ErrorBase;
    }
}
export declare interface S2Event<out STATE extends S2State, ID extends unknown> extends Event, WithId<ID> {
    readonly id: ID;
    readonly type: STATE;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.S2Event": unique symbol;
    } & WithId<unknown>["__doNotUseOrImplementIt"] & Event["__doNotUseOrImplementIt"];
}
export declare class S2EventSuccess<out STATE extends S2State, out COMMAND extends Command, ID extends unknown> implements Event {
    constructor(id: ID, type: COMMAND, from: STATE, to: STATE);
    get id(): ID;
    get type(): COMMAND;
    get from(): STATE;
    get to(): STATE;
    readonly __doNotUseOrImplementIt: Event["__doNotUseOrImplementIt"];
}
export declare namespace S2EventSuccess {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <STATE extends S2State, COMMAND extends Command, ID extends unknown>() => S2EventSuccess<STATE, COMMAND, ID>;
    }
}
export declare class S2EventError<out STATE extends S2State, out COMMAND extends Command, ID extends unknown> implements Event {
    constructor(id: ID, type: COMMAND, from: STATE, to: STATE, error: S2Error);
    get id(): ID;
    get type(): COMMAND;
    get from(): STATE;
    get to(): STATE;
    get error(): S2Error;
    readonly __doNotUseOrImplementIt: Event["__doNotUseOrImplementIt"];
}
export declare namespace S2EventError {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <STATE extends S2State, COMMAND extends Command, ID extends unknown>() => S2EventError<STATE, COMMAND, ID>;
    }
}
export declare interface S2Role {
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.S2Role": unique symbol;
    };
}
export declare interface S2State {
    readonly position: number;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.S2State": unique symbol;
    };
}
export declare class S2InitTransition {
    constructor(to: S2StateValue, role: S2Role, action: S2TransitionValue);
    get to(): S2StateValue;
    get role(): S2Role;
    get action(): S2TransitionValue;
}
export declare namespace S2InitTransition {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2InitTransition;
    }
}
export declare class S2Transition {
    constructor(from: Nullable<S2StateValue>, to: S2StateValue, role: S2RoleValue, action: S2TransitionValue, result: Nullable<S2TransitionValue>);
    get from(): Nullable<S2StateValue>;
    get to(): S2StateValue;
    get role(): S2RoleValue;
    get action(): S2TransitionValue;
    get result(): Nullable<S2TransitionValue>;
}
export declare namespace S2Transition {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2Transition;
    }
}
export declare class S2TransitionValue {
    constructor(name: string);
    get name(): string;
}
export declare namespace S2TransitionValue {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2TransitionValue;
    }
}
export declare class S2RoleValue {
    constructor(name: string);
    get name(): string;
}
export declare namespace S2RoleValue {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2RoleValue;
    }
}
export declare class S2StateValue {
    constructor(name: string, position: number);
    get name(): string;
    get position(): number;
}
export declare namespace S2StateValue {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2StateValue;
    }
}
export declare interface WithId<ID extends unknown> {
    readonly id: ID;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.WithId": unique symbol;
    };
}
export declare function isAvailableTransitionByName(_this_: S2Automate, stateName: Nullable<string>, type: string): boolean;
export declare function s2StateName(status: Nullable<unknown>): Nullable<string>;
export declare interface WithS2Id<ID extends unknown> {
    s2Id(): ID;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.model.WithS2Id": unique symbol;
    };
}
export declare interface WithS2IdAndStatus<ID extends unknown, STATE> extends WithS2Id<ID>, WithS2State<STATE> {
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.model.WithS2IdAndStatus": unique symbol;
    } & WithS2State<unknown>["__doNotUseOrImplementIt"] & WithS2Id<unknown>["__doNotUseOrImplementIt"];
}
export declare interface WithS2Iteration {
    s2Iteration(): number;
    withS2Iteration(iteration: number): WithS2Iteration;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.model.WithS2Iteration": unique symbol;
    };
}
export declare interface WithS2State<STATE> {
    s2State(): STATE;
    readonly __doNotUseOrImplementIt: {
        readonly "s2.dsl.automate.model.WithS2State": unique symbol;
    };
}
export declare interface VectorAskQueryDTO {
    readonly question: string;
    readonly history: KtList<ChatMessageDTO>;
    readonly targetedFiles: KtList<string>;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.commons.kb.domain.command.VectorAskQueryDTO": unique symbol;
    };
}
export declare interface VectorAskedEventDTO extends Event {
    readonly item: string;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.commons.kb.domain.command.VectorAskedEventDTO": unique symbol;
    } & Event["__doNotUseOrImplementIt"];
}
export declare interface VectorCreateCommandDTO {
    readonly path: FilePathDTO;
    readonly file: Int8Array;
    readonly metadata: KtMap<string, string>;
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.commons.kb.domain.command.VectorCreateCommandDTO": unique symbol;
    };
}
export declare interface VectorCreatedEventDTO extends Event {
    readonly __doNotUseOrImplementIt: {
        readonly "io.komune.fs.commons.kb.domain.command.VectorCreatedEventDTO": unique symbol;
    } & Event["__doNotUseOrImplementIt"];
}