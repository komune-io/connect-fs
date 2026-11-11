declare function KtSingleton<T>(): T & (abstract new() => unknown);
export declare interface FileAskQuestionQueryDTO {
    readonly question: string;
    readonly history: ChatMessageDTO[];
    readonly metadata: ChatMetadataDTO;
}
export declare interface FileAskQuestionResultDTO {
    readonly item: string;
}
export declare interface DirectoryPathDTO {
    readonly objectType: string;
    readonly objectId: string;
    readonly directory: string;
}
export declare interface ChatMessageDTO {
    readonly content: string;
    readonly type: string;
}
export declare interface ChatMetadataDTO {
    readonly targetedFiles: string[];
}
export declare interface FilePathDTO {
    readonly objectType: string;
    readonly objectId: string;
    readonly directory: string;
    readonly name: string;
}
export declare class InvalidUrlException extends /* IllegalArgumentException */ Error {
    constructor(url: string);
}
export declare namespace InvalidUrlException {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => InvalidUrlException;
    }
}
export declare interface F2Consumer<T> {
}
export declare interface F2Function<in T, out R> {
}
export declare interface F2Supplier<R> {
}
export declare interface F2SupplierSingle<R> {
}
export declare class Batch {
    constructor(size?: number, concurrency?: number);
    get size(): number;
    get concurrency(): number;
    copy(size?: number, concurrency?: number): Batch;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace Batch {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => Batch;
    }
}
export declare function invokeF2Function<T, R>(fnc: F2Function<T, R>, msgs: Array<T>): Promise<Array<R>>;
export declare function invokeF2Consumer<T>(fnc: F2Consumer<T>, msgs: Array<T>): Promise<void>;
export declare function invokeF2Supplier<R>(fnc: F2Supplier<R>): Promise<Array<R>>;
export declare function invokeF2SupplierSingle<R>(fnc: F2SupplierSingle<R>): Promise<R>;
export type F2ClientType = "HTTP";
export declare namespace F2ClientType {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2ClientType;
    }
}
export declare interface F2Client {
    readonly type: F2ClientType;
}
/** @deprecated  */
export declare const initHook: {
    get(): unknown;
};
export declare interface Command extends Message {
}
export declare interface Event extends Message {
}
export declare interface Message {
}
export declare interface Problem extends Message {
}
export declare interface Query extends Message {
}
export declare interface EnvelopeDTO<T> extends WithEnvelopeId, WithEnvelopeData<T>, Message {
    readonly id: string;
    readonly data: T;
    readonly type: string;
    readonly datacontenttype?: string | null;
    readonly specversion?: string | null;
    readonly source?: string | null;
    readonly time?: string | null;
}
export declare class Envelope<T> implements EnvelopeDTO<T> {
    constructor(id: string, data: T, type: string, datacontenttype?: string | null, source?: string | null, time?: string | null);
    get id(): string;
    get data(): T;
    get type(): string;
    get datacontenttype(): string | null;
    get source(): string | null;
    get time(): string | null;
    get specversion(): string;
    copy(id?: string, data?: T, type?: string, datacontenttype?: string | null, source?: string | null, time?: string | null): Envelope<T>;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace Envelope {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <T>() => Envelope<T>;
    }
}
export declare interface WithEnvelopeData<T> {
    readonly data: T;
}
export declare interface WithEnvelopeId {
    readonly id: string;
}
export declare interface F2ErrorDTO extends Problem {
    readonly id?: string | null;
    readonly timestamp: string;
    readonly code: number;
    readonly requestId?: string | null;
    readonly message: string;
    readonly refusal?: F2RefusalDTO | null;
}
export declare interface F2RefusalDTO {
    readonly code: string;
}
export declare class F2Refusal implements F2RefusalDTO {
    constructor(code: string, parameters?: Record<string, string>, citers?: string[]);
    get code(): string;
    copy(code?: string, parameters?: Record<string, string>, citers?: string[]): F2Refusal;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace F2Refusal {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2Refusal;
    }
}
export declare class F2Error implements F2ErrorDTO {
    constructor(message: string, id?: string | null, timestamp?: string, code?: number, requestId?: string | null, refusal?: F2Refusal | null);
    get message(): string;
    get id(): string | null;
    get timestamp(): string;
    get code(): number;
    get requestId(): string | null;
    get refusal(): F2Refusal | null;
    toString(): string;
}
export declare namespace F2Error {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2Error;
    }
}
export declare function isRefusal(error: F2ErrorDTO): boolean;
export declare function refusalCode(error: F2ErrorDTO): string;
export declare abstract class HttpRefusal {
    private constructor();
    static get INVALID_PAYLOAD(): HttpRefusal & {
        get name(): "INVALID_PAYLOAD";
        get ordinal(): 0;
    };
    static get UNAUTHENTICATED(): HttpRefusal & {
        get name(): "UNAUTHENTICATED";
        get ordinal(): 1;
    };
    static get PAYMENT_REQUIRED(): HttpRefusal & {
        get name(): "PAYMENT_REQUIRED";
        get ordinal(): 2;
    };
    static get FORBIDDEN(): HttpRefusal & {
        get name(): "FORBIDDEN";
        get ordinal(): 3;
    };
    static get NOT_FOUND(): HttpRefusal & {
        get name(): "NOT_FOUND";
        get ordinal(): 4;
    };
    static get METHOD_NOT_ALLOWED(): HttpRefusal & {
        get name(): "METHOD_NOT_ALLOWED";
        get ordinal(): 5;
    };
    static get NOT_ACCEPTABLE(): HttpRefusal & {
        get name(): "NOT_ACCEPTABLE";
        get ordinal(): 6;
    };
    static get PROXY_UNAUTHENTICATED(): HttpRefusal & {
        get name(): "PROXY_UNAUTHENTICATED";
        get ordinal(): 7;
    };
    static get REQUEST_TIMEOUT(): HttpRefusal & {
        get name(): "REQUEST_TIMEOUT";
        get ordinal(): 8;
    };
    static get CONFLICT(): HttpRefusal & {
        get name(): "CONFLICT";
        get ordinal(): 9;
    };
    static get GONE(): HttpRefusal & {
        get name(): "GONE";
        get ordinal(): 10;
    };
    static get LENGTH_REQUIRED(): HttpRefusal & {
        get name(): "LENGTH_REQUIRED";
        get ordinal(): 11;
    };
    static get PRECONDITION_FAILED(): HttpRefusal & {
        get name(): "PRECONDITION_FAILED";
        get ordinal(): 12;
    };
    static get PAYLOAD_TOO_LARGE(): HttpRefusal & {
        get name(): "PAYLOAD_TOO_LARGE";
        get ordinal(): 13;
    };
    static get URI_TOO_LONG(): HttpRefusal & {
        get name(): "URI_TOO_LONG";
        get ordinal(): 14;
    };
    static get UNSUPPORTED_MEDIA_TYPE(): HttpRefusal & {
        get name(): "UNSUPPORTED_MEDIA_TYPE";
        get ordinal(): 15;
    };
    static get RANGE_NOT_SATISFIABLE(): HttpRefusal & {
        get name(): "RANGE_NOT_SATISFIABLE";
        get ordinal(): 16;
    };
    static get EXPECTATION_FAILED(): HttpRefusal & {
        get name(): "EXPECTATION_FAILED";
        get ordinal(): 17;
    };
    static get MISDIRECTED_REQUEST(): HttpRefusal & {
        get name(): "MISDIRECTED_REQUEST";
        get ordinal(): 18;
    };
    static get UNPROCESSABLE(): HttpRefusal & {
        get name(): "UNPROCESSABLE";
        get ordinal(): 19;
    };
    static get LOCKED(): HttpRefusal & {
        get name(): "LOCKED";
        get ordinal(): 20;
    };
    static get FAILED_DEPENDENCY(): HttpRefusal & {
        get name(): "FAILED_DEPENDENCY";
        get ordinal(): 21;
    };
    static get TOO_EARLY(): HttpRefusal & {
        get name(): "TOO_EARLY";
        get ordinal(): 22;
    };
    static get UPGRADE_REQUIRED(): HttpRefusal & {
        get name(): "UPGRADE_REQUIRED";
        get ordinal(): 23;
    };
    static get PRECONDITION_REQUIRED(): HttpRefusal & {
        get name(): "PRECONDITION_REQUIRED";
        get ordinal(): 24;
    };
    static get TOO_MANY_REQUESTS(): HttpRefusal & {
        get name(): "TOO_MANY_REQUESTS";
        get ordinal(): 25;
    };
    static get HEADERS_TOO_LARGE(): HttpRefusal & {
        get name(): "HEADERS_TOO_LARGE";
        get ordinal(): 26;
    };
    static get UNAVAILABLE_FOR_LEGAL_REASONS(): HttpRefusal & {
        get name(): "UNAVAILABLE_FOR_LEGAL_REASONS";
        get ordinal(): 27;
    };
    static get UNEXPECTED(): HttpRefusal & {
        get name(): "UNEXPECTED";
        get ordinal(): 28;
    };
    static get UNAVAILABLE(): HttpRefusal & {
        get name(): "UNAVAILABLE";
        get ordinal(): 29;
    };
    static values(): [typeof HttpRefusal.INVALID_PAYLOAD, typeof HttpRefusal.UNAUTHENTICATED, typeof HttpRefusal.PAYMENT_REQUIRED, typeof HttpRefusal.FORBIDDEN, typeof HttpRefusal.NOT_FOUND, typeof HttpRefusal.METHOD_NOT_ALLOWED, typeof HttpRefusal.NOT_ACCEPTABLE, typeof HttpRefusal.PROXY_UNAUTHENTICATED, typeof HttpRefusal.REQUEST_TIMEOUT, typeof HttpRefusal.CONFLICT, typeof HttpRefusal.GONE, typeof HttpRefusal.LENGTH_REQUIRED, typeof HttpRefusal.PRECONDITION_FAILED, typeof HttpRefusal.PAYLOAD_TOO_LARGE, typeof HttpRefusal.URI_TOO_LONG, typeof HttpRefusal.UNSUPPORTED_MEDIA_TYPE, typeof HttpRefusal.RANGE_NOT_SATISFIABLE, typeof HttpRefusal.EXPECTATION_FAILED, typeof HttpRefusal.MISDIRECTED_REQUEST, typeof HttpRefusal.UNPROCESSABLE, typeof HttpRefusal.LOCKED, typeof HttpRefusal.FAILED_DEPENDENCY, typeof HttpRefusal.TOO_EARLY, typeof HttpRefusal.UPGRADE_REQUIRED, typeof HttpRefusal.PRECONDITION_REQUIRED, typeof HttpRefusal.TOO_MANY_REQUESTS, typeof HttpRefusal.HEADERS_TOO_LARGE, typeof HttpRefusal.UNAVAILABLE_FOR_LEGAL_REASONS, typeof HttpRefusal.UNEXPECTED, typeof HttpRefusal.UNAVAILABLE];
    static valueOf(value: string): HttpRefusal;
    get name(): "INVALID_PAYLOAD" | "UNAUTHENTICATED" | "PAYMENT_REQUIRED" | "FORBIDDEN" | "NOT_FOUND" | "METHOD_NOT_ALLOWED" | "NOT_ACCEPTABLE" | "PROXY_UNAUTHENTICATED" | "REQUEST_TIMEOUT" | "CONFLICT" | "GONE" | "LENGTH_REQUIRED" | "PRECONDITION_FAILED" | "PAYLOAD_TOO_LARGE" | "URI_TOO_LONG" | "UNSUPPORTED_MEDIA_TYPE" | "RANGE_NOT_SATISFIABLE" | "EXPECTATION_FAILED" | "MISDIRECTED_REQUEST" | "UNPROCESSABLE" | "LOCKED" | "FAILED_DEPENDENCY" | "TOO_EARLY" | "UPGRADE_REQUIRED" | "PRECONDITION_REQUIRED" | "TOO_MANY_REQUESTS" | "HEADERS_TOO_LARGE" | "UNAVAILABLE_FOR_LEGAL_REASONS" | "UNEXPECTED" | "UNAVAILABLE";
    get ordinal(): 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29;
    get status(): number;
    get refusal(): string;
}
export declare namespace HttpRefusal {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => HttpRefusal;
    }
    abstract class Companion extends KtSingleton<Companion.$metadata$.constructor>() {
        private constructor();
    }
    namespace Companion {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            abstract class constructor {
                get DOMAIN(): string;
                ofStatus(status: number): HttpRefusal;
                private constructor();
            }
        }
    }
}
export declare class F2Exception extends /* RuntimeException */ Error {
    constructor(error: F2ErrorDTO, cause?: Error | null);
    get error(): F2ErrorDTO;
}
export declare namespace F2Exception {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2Exception;
    }
}
export declare interface Match<T> {
    readonly isNegative: boolean;
    map<R>(transform: (value: T) => R): Match<R>;
    not(): Match<T>;
    and(match: Match<T>): Match<T>;
    or(match: Match<T>): Match<T>;
}
export declare interface SortDTO {
    readonly property: string;
    readonly ascending: boolean;
    readonly nullsFirst?: boolean | null;
}
export declare interface PageDTO<out OBJECT> {
    readonly total: number;
    readonly items: OBJECT[];
}
export declare class Page<out OBJECT> implements PageDTO<OBJECT> {
    constructor(total: number, items: OBJECT[]);
    get total(): number;
    get items(): OBJECT[];
    copy(total?: number, items?: OBJECT[]): Page<OBJECT>;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace Page {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <OBJECT>() => Page<OBJECT>;
    }
}
export declare interface PageQueryDTO extends Query {
    readonly pagination?: OffsetPaginationDTO | null;
}
export declare interface PageQueryResultDTO<out OBJECT> extends Event, PageDTO<OBJECT> {
    readonly total: number;
    readonly items: OBJECT[];
    readonly pagination?: OffsetPaginationDTO | null;
}
export declare class PageQuery implements PageQueryDTO {
    constructor(pagination?: OffsetPaginationDTO | null);
    get pagination(): OffsetPaginationDTO | null;
    copy(pagination?: OffsetPaginationDTO | null): PageQuery;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace PageQuery {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PageQuery;
    }
}
export declare class PageQueryResult<out OBJECT> implements PageQueryResultDTO<OBJECT> {
    constructor(pagination: OffsetPagination | null, total: number, items: OBJECT[]);
    get pagination(): OffsetPagination | null;
    get total(): number;
    get items(): OBJECT[];
    copy(pagination?: OffsetPagination | null, total?: number, items?: OBJECT[]): PageQueryResult<OBJECT>;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace PageQueryResult {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <OBJECT>() => PageQueryResult<OBJECT>;
    }
}
export declare interface Pagination {
}
export declare interface OffsetPaginationDTO extends Pagination {
    readonly offset: number;
    readonly limit: number;
}
export declare interface PagePaginationDTO extends Pagination {
    readonly page?: number | null;
    readonly size?: number | null;
}
export declare class OffsetPagination implements OffsetPaginationDTO {
    constructor(offset: number, limit: number);
    get offset(): number;
    get limit(): number;
    copy(offset?: number, limit?: number): OffsetPagination;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace OffsetPagination {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OffsetPagination;
    }
}
export declare class PagePagination implements PagePaginationDTO {
    constructor(page?: number | null, size?: number | null);
    get page(): number | null;
    get size(): number | null;
    copy(page?: number | null, size?: number | null): PagePagination;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace PagePagination {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PagePagination;
    }
}
export declare function refusalParameters(refusal: F2RefusalDTO): ReadonlyMap<string, string>;
export declare function refusalCiters(refusal: F2RefusalDTO): ReadonlyArray<string>;
export declare interface HttpClientBuilder {
    build(urlBase: string): HttpF2Client;
}
export declare class HttpF2Client implements F2Client {
    private constructor();
    get urlBase(): string;
    get type(): F2ClientType;
}
export declare namespace HttpF2Client {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => HttpF2Client;
    }
}
export declare interface F2UploadCommand<COMMAND> {
    readonly command: COMMAND;
    readonly fileMap: Record<string, F2FilePart[]>;
}
export declare class F2UploadSingleCommand<COMMAND> implements F2UploadCommand<COMMAND> {
    constructor(command: COMMAND, file: F2FilePart);
    get command(): COMMAND;
    get file(): F2FilePart;
    get fileMap(): Record<string, F2FilePart[]>;
    copy(command?: COMMAND, file?: F2FilePart): F2UploadSingleCommand<COMMAND>;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace F2UploadSingleCommand {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <COMMAND>() => F2UploadSingleCommand<COMMAND>;
    }
}
export declare class F2UploadMultipleCommand<COMMAND> implements F2UploadCommand<COMMAND> {
    constructor(command: COMMAND, files: F2FilePart[]);
    get command(): COMMAND;
    get files(): F2FilePart[];
    get fileMap(): Record<string, F2FilePart[]>;
    copy(command?: COMMAND, files?: F2FilePart[]): F2UploadMultipleCommand<COMMAND>;
    toString(): string;
    hashCode(): number;
    equals(other?: unknown): boolean;
}
export declare namespace F2UploadMultipleCommand {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <COMMAND>() => F2UploadMultipleCommand<COMMAND>;
    }
}
export declare class F2FilePart {
    private constructor();
    get name(): string;
    get contentType(): string | null;
    static fromBytes(name: string, bytes: Int8Array, contentType?: string | null): F2FilePart;
}
export declare namespace F2FilePart {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => F2FilePart;
    }
}
export declare function httpClientBuilderDefault(): HttpClientBuilder;
export declare function httpF2Supplier<RESPONSE>(client: HttpF2Client, route: string): F2Supplier<RESPONSE>;
export declare function httpF2Function<QUERY, RESPONSE>(client: HttpF2Client, route: string): F2Function<QUERY, RESPONSE>;
export declare function httpF2Consumer<QUERY>(client: HttpF2Client, route: string): F2Consumer<QUERY>;
export declare abstract class AuthRealm {
    private constructor();
    get serverUrl(): string;
    get realmId(): string;
    get clientId(): string;
    get redirectUrl(): string | null;
}
export declare namespace AuthRealm {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => AuthRealm;
    }
}
export declare class AuthRealmPassword extends AuthRealm.$metadata$.constructor {
    constructor(serverUrl: string, realmId: string, redirectUrl: string, clientId: string, username: string, password: string);
    get serverUrl(): string;
    get realmId(): string;
    get redirectUrl(): string;
    get clientId(): string;
    get username(): string;
    get password(): string;
    toString(): string;
}
export declare namespace AuthRealmPassword {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => AuthRealmPassword;
    }
}
export declare class AuthRealmClientSecret extends AuthRealm.$metadata$.constructor {
    constructor(serverUrl: string, realmId: string, clientId: string, redirectUrl: string | null | undefined, clientSecret: string, isPublic?: boolean);
    get serverUrl(): string;
    get realmId(): string;
    get clientId(): string;
    get redirectUrl(): string | null;
    get clientSecret(): string;
    get isPublic(): boolean;
    toString(): string;
}
export declare namespace AuthRealmClientSecret {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => AuthRealmClientSecret;
    }
}
export declare interface Automate {
    getAvailableTransitions(state: S2State): Array<S2Transition>;
    isAvailableTransition(currentState: S2State, msg: Message): boolean;
    isAvailableInitTransition(command: Message): boolean;
    isFinalState(state: S2State): boolean;
    isSameState(from: S2State | null, to: S2State): boolean;
}
export type ErrorCategory = "Rejected" | "Transient" | "Indeterminate" | "Conflict";
export declare namespace ErrorCategory {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ErrorCategory;
    }
}
export declare class S2Automate implements Automate {
    constructor(name: string, version: string | null, transitions: Array<S2Transition>);
    get name(): string;
    get version(): string | null;
    get transitions(): Array<S2Transition>;
    get withResultAsAction(): boolean;
    getAvailableTransitions(state: S2State): Array<S2Transition>;
    isAvailableTransition(currentState: S2State, msg: Message): boolean;
    isAvailableInitTransition(command: Message): boolean;
    isFinalState(state: S2State): boolean;
    isSameState(from: S2State | null, to: S2State): boolean;
}
export declare namespace S2Automate {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => S2Automate;
    }
}
export declare interface S2InitCommand extends Command {
}
export declare interface S2Command<ID extends unknown> extends Command, WithId<ID> {
}
export declare interface S2Error {
    readonly type: string;
    readonly description: string;
    readonly date: string;
    readonly payload: Record<string, string>;
    readonly cause?: Error | null;
}
export declare class S2ErrorBase implements S2Error {
    constructor(type: string, description: string, date: string, payload: Record<string, string>, cause?: Error | null);
    get type(): string;
    get description(): string;
    get date(): string;
    get payload(): Record<string, string>;
    get cause(): Error | null;
    toString(): string;
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
}
export declare class S2EventSuccess<out STATE extends S2State, out COMMAND extends Command, ID extends unknown> implements Event {
    constructor(id: ID, type: COMMAND, from: STATE, to: STATE);
    get id(): ID;
    get type(): COMMAND;
    get from(): STATE;
    get to(): STATE;
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
}
export declare namespace S2EventError {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new <STATE extends S2State, COMMAND extends Command, ID extends unknown>() => S2EventError<STATE, COMMAND, ID>;
    }
}
export declare interface S2Role {
}
export declare interface S2State {
    readonly position: number;
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
    constructor(from: S2StateValue | null, to: S2StateValue, role: S2RoleValue, action: S2TransitionValue, result?: S2TransitionValue | null);
    get from(): S2StateValue | null;
    get to(): S2StateValue;
    get role(): S2RoleValue;
    get action(): S2TransitionValue;
    get result(): S2TransitionValue | null;
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
}
export declare interface WithS2Id<ID extends unknown> {
    s2Id(): ID;
}
export declare interface WithS2IdAndStatus<ID extends unknown, STATE> extends WithS2Id<ID>, WithS2State<STATE> {
}
export declare interface WithS2Iteration {
    s2Iteration(): number;
    withS2Iteration(iteration: number): WithS2Iteration;
}
export declare interface WithS2State<STATE> {
    s2State(): STATE;
}
export declare interface VectorAskQueryDTO {
    readonly question: string;
    readonly history: ChatMessageDTO[];
    readonly targetedFiles: string[];
}
export declare interface VectorAskedEventDTO extends Event {
    readonly item: string;
}
export declare interface VectorCreateCommandDTO {
    readonly path: FilePathDTO;
    readonly file: Int8Array;
    readonly metadata: Record<string, string>;
}
export declare interface VectorCreatedEventDTO extends Event {
}